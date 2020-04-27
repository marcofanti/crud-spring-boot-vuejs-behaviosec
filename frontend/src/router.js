import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  mode: "history",
  routes: [
    {
      path: "/",
      alias: "/users",
      name: "users",
      component: () => import("./components/UsersList")
    },
    {
      path: "/users/:id",
      name: "user-details",
      component: () => import("./components/User")
    },
    {
      path: "/add",
      name: "add",
      component: () => import("./components/AddUser")
    }
  ]
});
