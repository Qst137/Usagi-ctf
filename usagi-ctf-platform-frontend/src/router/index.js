import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    component: () => import("@/components/Main.vue"),
    children: [
      { path: "/", redirect: "/home" },
      {
        path: "/home",
        name: "home",
        component: () => import("@/views/Home.vue"),
      },
      {
        path: "/login",
        name: "login",
        component: () => import("@/views/Login.vue"),
      },
      {
        path: "/register",
        name: "register",
        component: () => import("@/views/Register.vue"),
      },
      {
        path: "/rank",
        meta: { requiresAuth: true },
        children: [
          {
            path: "/rank",
            name: "rank",
            component: () => import("@/views/Rank.vue"),
          },
          {
            path: "/rank/:gameID",
            component: () => import("@/views/RankByID.vue"),
            props: (route) => ({ gameID: route.params.gameID }),
          },
        ],
      },
      {
        path: "/user",
        meta: { requiresAuth: true },
        name: "user",
        component: () => import("@/views/User.vue"),
      },
      {
        path: "/admin",
        meta: { requiresAuth: true },
        name: "admin",
        component: () => import("@/views/Admin.vue"),
      },
      {
        path: "/problem",
        meta: { requiresAuth: true },
        children: [
          {
            path: "/problem",
            name: "problem",
            component: () => import("@/views/Game.vue"),
          },
          {
            path: "/problem/:gameID",
            component: () => import("@/views/Problem.vue"),
            props: (route) => ({ gameID: route.params.gameID }),
          },
        ],
      },
    ],
  },
  {
    path: "/404",
    name: "404",
    component: () => import("@/views/NotFound.vue"),
  },
  // 所有未定义路由，全部重定向到 404 页
  { path: "/:pathMatch(.*)*", redirect: "/404" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from) => {
  if (to.meta.requiresAuth) {
    if (localStorage.getItem("Token") == null) {
      return {
        path: "/login",
      };
    }
  }
});

export default router;
