<script setup lang="ts">
import Nav from '../src/components/Nav.vue';
import { ref, computed, onMounted } from 'vue';
import StaffMemeberDataService from '../src/services/StaffMemeberDataService.js';

const result =ref('')
const error = ref('')
const searchTerm = ref("");

const onSearch = () => {
  markAsUsed(searchTerm.value.trim());
};

// Event Handlers
const onDetect = (detectedResult) => {
  markAsUsed(detectedResult);
};

  function onError(err) {
    error.value = `[${err.name}]: `

    if (err.name === 'NotAllowedError') {
      error.value += 'you need to grant camera access permission'
    } else if (err.name === 'NotFoundError') {
      error.value += 'no camera on this device'
    } else if (err.name === 'NotSupportedError') {
      error.value += 'secure context required (HTTPS, localhost)'
    } else if (err.name === 'NotReadableError') {
      error.value += 'is the camera already in use?'
    } else if (err.name === 'OverconstrainedError') {
      error.value += 'installed cameras are not suitable'
    } else if (err.name === 'StreamApiNotSupportedError') {
      error.value += 'Stream API is not supported in this browser'
    } else if (err.name === 'InsecureContextError') {
      error.value += 'Camera access is only permitted in secure context. Use HTTPS or localhost rather than HTTP.'
    } else {
      error.value += err.message
    }
  }

  onMounted(()=>{
    data.value = StaffMemeberDataService.getAll();
  })

  const data = ref([]); // Empty array to hold data fetched from the backend

  // Fetch data from the backend on mount
const fetchData = async () => {
  try {
    const response = await StaffMemeberDataService.getAll(); // Replace with your API call
    data.value = response.data; // Update the list with the backend data
  } catch (err) {
    console.error("Error fetching data:", err);
    error.value = "Failed to load data from the server.";
  }
};

// Computed property for filtered data
const filteredData = computed(() =>
  data.value.filter((item) =>
    item.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
    item.fnumber.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
);

// Validate fnumber
const validateCode = (input: string) => {
  if (input.trim().toUpperCase().startsWith("ID")) {
    result.value = `Do not include 'ID' in the code. Enter the rest of the code (e.g., '12345' instead of 'ID12345').`;
    return false;
  }
  return true;
};

// create validation ganstlocal list before querying the server
// Helper function to mark code as used
const markAsUsed = async (fnumber: string) => {

  const item = data.value.find((row) => row.fnumber === fnumber && !row.status);

  if (item) {
    try {
      // Call backend to update the status
      const response = await StaffMemeberDataService.updateStatus(item.fnumber, { status: true });
      console.log(response)
      if (response.status === 200) {
        // Successfully updated, now refresh the list
        fetchData();
        result.value = `${fnumber} matched and marked as used!`;
      } else {
        result.value = `Failed to update the status for ${fnumber}.`;
      }
    } catch (err) {
      console.error("Error updating status:", err);
      result.value = `Error updating the status for ${fnumber}.`;
    }
  } else {
    result.value = `${fnumber} not found or already used.`;
  }
};

// Call fetchData on mount
onMounted(() => {
  fetchData();
});
  
  // Sample Table Data
// const data = ref([
//   { name: "Mwamba Changala", details: "Sample Detail 1", code: "5543231", used:false },
//   { name: "Mweeemba Hambulo", details: "Sample Detail 2", code: "5095808", used:false },
//   { name: "John Doe", details: "Sample Detail 3", code: "CODE789", used:false },
// ]);

// // Computed Property for Filtered Data
// const filteredData = computed(() =>

//   data.value.filter((item) =>

//     item.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
//     item.code.toLowerCase().includes(searchTerm.value.toLowerCase())

//   )


// );

// const validateCode = (input) => {
//   // Check if input starts with 'ID'
//   if (input.trim().toUpperCase().startsWith("ID")) {
//     result.value = `Do not include 'ID' in the code. Enter the rest of the code (e.g., '12345' instead of 'ID12345').`;
//     return false;
//   }
// }
// // Helper Function to Mark Code as Used
// const markAsUsed = (code) => {
//   const item = data.value.find((row) => row.code === code && !row.used);
//        console.log(item)
//   if (item) {
//     item.used = true; // Mark the row as used
//     result.value = `${code} matched and marked as used!`;
//   } else {
//     result.value = `${code} not found or already used.`;
//   }

  
// };

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
            :track="paintBoundingBox"
            @detect="onDetect"
            @error="onError"
          ></qrcode-stream>
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
          <tr v-for="(item, index) in filteredData" :key="index" :class="{ 'table-used': item.used }">
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
</style>ba