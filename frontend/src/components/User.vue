<template>
  <div v-if="currentUser" class="edit-form">
    <h4>User</h4>
    <form>
      <div class="form-group">
        <label for="email">Email</label>
        <input type="text" class="form-control" id="email"
               v-model="currentUser.email"
        />
      </div>
      <div class="form-group">
        <label for="trainingphrase">Training Phrase</label>
        <input type="text" class="form-control" id="trainingphrase"
               v-model="currentUser.trainingphrase"
        />
      </div>

 <!--     <div class="form-group">
        <label><strong>Status:</strong></label>
        {{ currentUser.published ? "Published" : "Pending" }}
      </div> -->
    </form>
    <button class="badge badge-danger mr-2"
            @click="deleteUser"
    >
      Delete
    </button>

    <button type="submit" class="badge badge-success"
            @click="updateUser"
    >
      Update
    </button>
    <p>{{ message }}</p>
  </div>

  <div v-else>
    <br />
    <p>Please click on a User...</p>
  </div>
</template>

<script>
  import UserDataService from "../services/UserDataService";
  import bw from "../collector.min";

  export default {
    name: "user",
    data() {
      return {
        currentUser: null,
        message: ''
      };
    },
    methods: {
      getUser(id) {
        UserDataService.get(id)
                .then(response => {
                  this.currentUser = response.data;
                  console.log(response.data);
                })
                .catch(e => {
                  console.log(e);
                });
      },

      updateUser() {
        var time = new Date();
        var userId = this.username + this.password;
        var sessionId = userId + time.getTime(); // Example sessionId, you don't need to have a timestamp but all sessions should have a different name
        console.log("=======bw.getData()========\n", bw.getData());
        var timing = bw.getData();
        var ipAddress = window.location.host; // Example ip
        var userAgent = window.navigator.userAgent;
        var urlParam =
                "userId=" +
                userId +
                "&timing=" +
                timing +
                "&sessionId=" +
                sessionId +
                "&userAgent=" +
                userAgent +
                "&ip=" +
                ipAddress +
                "&reportFlags=0&operatorFlags=1";
        var url = "http://localhost:8098/BehavioSenseAPI/GetAjaxAsync"; // Use your server's endpoint for GetReport
        var xhr = new XMLHttpRequest();

        xhr.open("POST", url, true); // Settle the request, it needs to be a POST request

        xhr.responseType = "json";

        xhr.send(urlParam); // Send the POST request to your server's endpoint for GetReport

        xhr.onreadystatechange = function() {
          console.log("POST (logged: readyState and status):", xhr.readyState, xhr.status);
          if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            alert("Done.")
          }
        }

        UserDataService.update(this.currentUser.id, this.currentUser)
                .then(response => {
                  console.log(response.data);
                  this.message = 'The User was updated successfully!';
                })
                .catch(e => {
                  console.log(e);
                });
      },

      deleteUser() {
        UserDataService.delete(this.currentUser.id)
                .then(response => {
                  console.log(response.data);
                  this.$router.push({ name: "users" });
                })
                .catch(e => {
                  console.log(e);
                });
      }
    },
    mounted() {
      this.message = '';
      this.getUser(this.$route.params.id);
    }
  };
</script>

<style>
  .edit-form {
    max-width: 300px;
    margin: auto;
  }
</style>
