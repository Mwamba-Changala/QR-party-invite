<script setup>
import Nav from '../src/components/Nav.vue';
import { ref, computed, onMounted } from 'vue';
import StaffMemeberDataService from './services/StaffMemeberDataService';
import Swal from 'sweetalert2';

const result = ref('');
const paused = ref(false);
const showScanConfirmation = ref(false);

const error = ref('');
const searchTerm = ref('');
const data = ref([]); // Reactive array for staff members

// Fetch data from the backend
const resetFields = () => {
  searchTerm.value = ''; // Clear search field
        paused.value = false;
        fetchData(); // Refresh the data list
};

// Fetch data from the backend
const fetchData = async () => {
  try {
    const response = await StaffMemeberDataService.getAll();
    data.value = response.data; // Update staff data
    console.log("Fetched staff members:", response.data);
  } catch (err) {
    console.error("Error fetching staff members:", err);
    error.value = `Error fetching staff members: ${err.message}`;
  }
};

// SweetAlert helpers
const showAlert = (title, text, icon) => {
  Swal.fire({
    title,
    text,
    icon,
    timer: 2000, // Alert will close after 2 seconds
    showConfirmButton: false, // No confirm button
    toast: true, // Make it look like a notification (optional)
    position: 'top-right', // Position it at the top-right corner (optional)
  });
};

// Mark a staff member as used
const markAsUsed = async (fnumber) => {
  const item = data.value.find((row) => row.fnumber === fnumber && !row.status);
  const itemExists = data.value.find((row) => row.fnumber === fnumber);
console.log(itemExists)
  if (!itemExists) {
    // Not found in the list
    showAlert('Not Found', `The F-Number ${fnumber} was not found in the list.`, 'error');
    consloe.log(itemExists)
    resetFields();
    return;
  }

  if (itemExists.status) {
    // Already confirmed
    showAlert('Already Confirmed', `The F-Number ${fnumber} has already been confirmed.`, 'info');
    resetFields();
    consloe.log(item)
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



// Filter data based on search term
const filteredData = computed(() =>
  data.value.filter((item) =>
    item.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
    item.fnumber.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
);

// Handlers for camera events
const onCameraOn = () => {
  showScanConfirmation.value = false;
};

const onCameraOff = () => {
  showScanConfirmation.value = true;
};

const onError = (err) => {
  console.error(err);
  error.value = `[${err.name}]: ${err.message}`;
};

// Handle detection from the QR scanner
const onDetect = (detectedCodes) => {
  const detectedValues = detectedCodes.map((code) => code.rawValue);
  const detectedString = detectedValues.join(','); // Combine multiple codes if needed
  console.log("Detected:", detectedString);

  searchTerm.value = detectedString; // Push detected value to the search field
  paused.value = true; // Pause scanning
  markAsUsed(detectedString); // Trigger marking process
};

// Handle manual search
const onSearch = () => {
  console.log("Search triggered for:", searchTerm.value.trim());
  markAsUsed(searchTerm.value.trim());
};

// Fetch data when the component is mounted
onMounted(() => {
  fetchData();
});
</script>


<template>
 
    <div class="app-container bg-light">
        <Nav />

        <div class="container pt-4 pb-4">
            <p style="color: red">{{ error }}</p>

            <div class="d-flex justify-content-between align-items-start">
      
              <!-- QR Code Scanner -->
      <div class="scanner-container">
        <p>Last result: <b>{{ result }}</b></p>
        
        <div class="scanner-box">
          <qrcode-stream
            :paused="paused"
            @camera-on="onCameraOn"
            @camera-off="onCameraOff"
            @detect="onDetect"
            @error="onError"
          >
     
        </qrcode-stream>
        </div>
      </div>

      <!-- Search Input -->
      <div class="search-container">
        <label for="search">Search:</label>
        <input
          id="search"
          type="text"
          v-model="searchTerm"
          placeholder="F Number..."
          @input="onSearch"
          class="form-control"
        />
      </div>
    </div>

    <!-- Table Below -->
    <div class="table-container mt-4">
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>F-Number</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in filteredData" :key="index" :class="{ 'table-used': item.status }">
            <td>{{ index + 1 }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.fnumber }}</td>
            <td>
              <span v-if="item.status " class="text-muted">Verified</span>
              <span v-else>Not Verified</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  
            
        </div>
    </div>
 
</template>


<style>
@import '@/assets/base.css';


.scanner-container {
  width: 50%;
}

.scanner-container {
  width: 50%;
}

.scanner-box {
  border: 2px solid black;
  width: 100%;
  height: 300px; /* Adjust scanner box height */
}

.search-container {
  width: 45%;
}

.form-control {
  margin-top: 8px;
}

.table-container {
  width: 100%;
}

.table-used {
  text-decoration: line-through;
  background-color: #f8f9fa;
}

.text-muted {
  color: gray;
}

.scan-confirmation {
  position: absolute;
  width: 100%;
  height: 100%;

  background-color: rgba(255, 255, 255, 0.8);

  display: flex;
  flex-flow: row nowrap;
  justify-content: center;
}
</style>