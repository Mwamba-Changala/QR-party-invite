import http from "./http-common";

class StaffMemeberDataService {
  getAll() {
    return http.get("/invite/all");
  }

  updateStatus(id, status) {
    console.log(status)
    return http.put(`/invite/${id}`, status );
  }

  
}

export default new StaffMemeberDataService();