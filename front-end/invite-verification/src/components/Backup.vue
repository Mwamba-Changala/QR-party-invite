<script setup lang="ts">
import Nav from '../src/components/Nav.vue';
import { ref, computed, onMounted } from 'vue';
import StaffMemeberDataService from './services/StaffMemeberDataService';

// Types
interface StaffMember {
  name: string;
  fnumber: string;
  status: boolean;
}

const result = ref<string>('');
const paused = ref(false);
const showScanConfirmation = ref(false);

const error = ref<string>('');
const searchTerm = ref<string>('');
const data = ref<StaffMember[]>([]);

// Methods
const onCameraOn = () => {
  showScanConfirmation.value = false;
};

const onCameraOff = () => {
  showScanConfirmation.value = true;
};

const onSearch = () => {
  console.log(searchTerm.value.trim())
  markAsUsed(searchTerm.value.trim());
};

// Fetch data from the backend
const fetchData = async () => {
  try {
    const response = await StaffMemeberDataService.getAll();
    const staffMembers: StaffMember[] = response.data;
    data.value = staffMembers;
    console.log(staffMembers);
  } catch (error) {
    console.error("Error fetching staff members:", error);
  }
};

// Mark a staff member as used
const markAsUsed = async (fnumber: string): Promise<void> => {
  const item = data.value.find((row) => row.fnumber === fnumber && !row.status);

  if (item) {
    try {
      const response = await StaffMemeberDataService.updateStatus(item.fnumber, { status: true });
      if (response.status === 200) {
        
        result.value = `${fnumber} matched and marked as used!`;
        searchTerm.value = '';
        paused.value = false;
        fetchData();
      } else {
        result.value = `Failed to update the status for ${fnumber}.`;
      }
    } catch (err) {
      result.value = `Error updating the status for ${fnumber}.`;
    }
  } else {
    fetchData();
    paused.value = false;
    result.value = `${fnumber} not found or already used.`;
  }
};

const filteredData = computed(() =>
  data.value.filter((item) =>
    item.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
    item.fnumber.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
);

const onDetect = (detectedResult: string) => {
  const resultArray = detectedResult.map((code) => code.rawValue); 
  const resultString = resultArray.join(','); // Join with a comma or any separator you prefer 
  const jsonString = JSON.stringify(resultString);
  const plainString = jsonString.replace(/(^"|"$)/g, '');
  console.log(plainString)
  paused.value = true;
  markAsUsed(plainString);  
  // await timeout(500);
  
  
};

const onError = (err: Error & { name?: string }) => {
  error.value = `[${err.name}]: ${err.message}`;
};

// Initialize data on mount
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