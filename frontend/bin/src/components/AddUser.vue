<template>
  <div class="submit-form">
    <div v-if="!submitted">
      <div class="form-group">
        <label for="email">Email</label>
        <input
          type="text"
          class="form-control"
          id="email"
          required
          v-model="user.email"
          name="title"
        />
      </div>

      <div class="form-group">
        <label for="trainingphrase">Training Phrase</label>
        <input
          class="form-control"
          id="trainingphrase"
          required
          v-model="user.trainingphrase"
          name="description"
        />
      </div>

      <button @click="saveUser" class="btn btn-success">Submit</button>
    </div>

    <div v-else>
      <h4>You submitted successfully!</h4>
      <button class="btn btn-success" @click="newUser">Add</button>
    </div>
  </div>
</template>

<script>
import UserDataService from "../services/UserDataService";

export default {
  name: "add-user",
  data() {
    return {
      user: {
        id: null,
        email: "",
        trainingphrase: "",
       },
      submitted: false
    };
  },
  methods: {
    saveUser() {
      var data = {
        email: this.user.email,
        trainingphrase: this.user.trainingphrase
      };

      UserDataService.create(data)
        .then(response => {
          this.user.id = response.data.id;
          console.log(response.data);
          this.submitted = true;
        })
        .catch(e => {
          console.log(e);
        });
    },
    
    newUser() {
      this.submitted = false;
      this.user = {};
    }
  }
};
</script>

<style>
.submit-form {
  max-width: 300px;
  margin: auto;
}
</style>
