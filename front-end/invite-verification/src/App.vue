<script setup>
import Nav from '../src/components/Nav.vue';
import { ref, computed, onMounted } from 'vue';
import StaffMemeberDataService from './services/StaffMemeberDataService';
import Swal from 'sweetalert2';

const result = ref('');
const paused = ref(false);
const showScanConfirmation = ref(false);
const isSubmittingSearch = ref(false);
const isSubmittingRefresh = ref(false);

const error = ref('');
const searchTerm = ref('');
const data = ref([]); // Reactive array for staff members
const filteredResults = ref([]); // Reactive array for filtered results

// Fetch data from the backend
const resetFields = () => {
  searchTerm.value = ''; // Clear search field
  paused.value = false;
  isSubmittingSearch.value = false;
  filteredResults.value = data.value; // Reset filtered results to all data
  fetchData(); // Refresh the data list
};

const fetchData = async () => {
  try {
    const response = await StaffMemeberDataService.getAll();
    data.value = response.data; // Update staff data
    filteredResults.value = response.data; // Display all data initially
    console.log("Fetched staff members:", response.data);
  } catch (err) {
    console.error("Error fetching staff members:", err);
    error.value = `Error fetching staff members: ${err.message}`;
    showAlert(`Failed to refresh the list. Please try again. ${err.message}`, 'error');
  } finally {
    isSubmittingRefresh.value = false;
  }
};

// SweetAlert helpers
const showAlert = (title, text, icon) => {
  Swal.fire({
    title,
    text,
    icon,
    timer: 5000,
    showConfirmButton: false,
    toast: true,
    position: 'top-right',
  });
};

// Confirmation dialog
const showConfirmationDialog = (title, text, callback) => {
  Swal.fire({
    title,
    text,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Yes',
    cancelButtonText: 'No',
  }).then((result) => {
    if (result.isConfirmed) {
      callback(); // Execute the provided callback if confirmed
    }
  });
};

// Mark a staff member as used
const markAsUsed = async (fnumber) => {
  const item = data.value.find((row) => row.fnumber === fnumber && !row.status);
  const itemExists = data.value.find((row) => row.fnumber === fnumber);
  console.log(itemExists);

  if (!itemExists) {
    // Not found in the list
    showAlert('Not Found', `The F-Number ${fnumber} was not found in the list.`, 'error');
    resetFields();
    return;
  }

  if (itemExists.status) {
    // Already confirmed
    showAlert('Already Confirmed', `The F-Number ${fnumber} has already been confirmed.`, 'warn');
    resetFields();
    return;
  }

  try {
    const response = await StaffMemeberDataService.updateStatus(item.fnumber, { status: true });
    if (response.status === 200) {
      // Successfully marked as used
      showAlert('Confirmed', `The F-Number ${fnumber} has been successfully confirmed!`, 'success');
      result.value = `${fnumber} matched and marked as used!`;
      resetFields();
    } else {
      result.value = `Failed to update the status for ${fnumber}.`;
      showAlert('Error', `Failed to update the status for ${fnumber}.`, 'error');
      resetFields();
    }
  } catch (err) {
    console.error("Error updating status:", err);
    result.value = `Error updating the status for ${fnumber}.`;
    showAlert('Error', `An error occurred while updating the status for ${fnumber}.`, 'error');
    resetFields();
  }
};

// Handle manual search with confirmation
const onSearch = () => {
  const trimmedSearchTerm = searchTerm.value.trim();

  if (!trimmedSearchTerm) {
    showAlert('Empty Search', `Search term is empty. Please enter an F-number`, 'warning');
    return;
  }

  console.log("Search triggered for:", searchTerm.value);
  isSubmittingSearch.value = true;

  try {
    filteredResults.value = data.value.filter((item) =>
      item.name.toLowerCase().includes(trimmedSearchTerm.toLowerCase()) ||
      item.fnumber.toLowerCase().includes(trimmedSearchTerm.toLowerCase())
    );

    console.log("Filtered results:", filteredResults.value);

    // Ask for confirmation before marking the item as used
    showConfirmationDialog(
      'Confirm Search',
      `Do you want to confirm the F-Number ${trimmedSearchTerm}?`,
      () => markAsUsed(trimmedSearchTerm)
    );
  } finally {
    isSubmittingSearch.value = false;
  }
};

// Handle detection from the QR scanner
const onDetect = (detectedCodes) => {
  const detectedValues = detectedCodes.map((code) => code.rawValue);
  const detectedString = detectedValues.join(','); // Combine multiple codes if needed

  console.log("Detected:", detectedString);

  searchTerm.value = detectedString; // Update the search term field for visibility

  // Directly filter the results based on the detected string
  filteredResults.value = data.value.filter((item) =>
    item.name.toLowerCase().includes(detectedString.toLowerCase()) ||
    item.fnumber.toLowerCase().includes(detectedString.toLowerCase())
  );

  paused.value = true; // Pause scanning

  // Ask for confirmation before marking the item as used
  showConfirmationDialog(
    'Confirm Detection',
    `Do you want to confirm the F-Number ${detectedString}?`,
    () => markAsUsed(detectedString)
  );
};

// Fetch data when the component is mounted
onMounted(() => {
  fetchData();
});
</script>

<template>
  <Nav />
  <div class="app-container bg-light p-4">
    <div class="container">
      <p class="text-danger">{{ error }}</p>

      <div class="row g-4">
        <!-- QR Code Scanner -->
        <div class="col-md-6">
          <p>Last result: <b>{{ result }}</b></p>
          <div class="border border-dark rounded d-flex align-items-center justify-content-center" style="height: 300px;">
            <qrcode-stream
              :paused="paused"
              @camera-on="() => (showScanConfirmation = false)"
              @camera-off="() => (showScanConfirmation = true)"
              @detect="onDetect"
              @error="onError"
            ></qrcode-stream>
          </div>
        </div>

        <!-- Search Section -->
        <div class="col-md-6">
          <label for="search" class="form-label">Search:</label>
          <input
            id="search"
            type="text"
            v-model="searchTerm"
            placeholder="F Number..."
            class="form-control mb-3"
          />

          <div class="d-flex gap-2">
            <!-- Search Button -->
            <button
              type="button"
              class="btn btn-primary d-flex align-items-center"
              :disabled="isSubmittingSearch"
              @click="onSearch"
            >
              <span v-if="isSubmittingSearch" class="spinner-border spinner-border-sm me-2"></span>
              <span v-else>Search</span>
            </button>

            <!-- Refresh Button -->
            <button
              type="button"
              class="btn btn-secondary d-flex align-items-center"
              :disabled="isSubmittingRefresh"
              @click="fetchData"
            >
              <span v-if="isSubmittingRefresh" class="spinner-border spinner-border-sm me-2"></span>
              <span v-else>Refresh</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Table Section -->
      <div class="table-responsive mt-4">
        <table class="table table-bordered table-striped">
          <thead class="table-primary">
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>F-Number</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(item, index) in filteredResults"
              :key="index"
              :class="{ 'text-decoration-line-through bg-light': item.status }"
            >
              <td>{{ index + 1 }}</td>
              <td>{{ item.name }}</td>
              <td>{{ item.fnumber }}</td>
              <td>
                <span v-if="item.status" class="text-muted">Verified</span>
                <span v-else>Not Verified</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
