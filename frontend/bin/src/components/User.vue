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
