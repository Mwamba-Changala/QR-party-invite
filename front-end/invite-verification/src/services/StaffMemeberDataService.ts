import http from "./http-common";

// Define the interface for a Staff Member
export interface StaffMember {
  id: string; // Assuming `id` is a string; update if it's a number
  name: string;
  fnumber: string;
  status: boolean;
}

class StaffMemeberDataService {
  // Fetch all staff members
  getAll(){
   
    return http.get("/invite/all");
    
  }

  // Update the status of a staff member
  updateStatus(id: string, status: { status: boolean }): Promise<{ status: number }> {
    console.log(status);
    return http.put(`/invite/${id}`, status);
  }
}

export default new StaffMemeberDataService();
