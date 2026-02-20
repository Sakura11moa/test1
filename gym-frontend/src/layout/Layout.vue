<template>
  <div class="layout">
      <!--  头部-->
      <Header />
      <!--  主体  -->
      <div style="display: flex" >
        <!--  侧边栏  -->
        <Aside />
        <!--  内容区域  -->
        <div class="content">
          <router-view />
        </div>
      </div>
  </div>
</template>

<script>
import Header from "@/components/admin/Header";
import Aside from "@/components/admin/Aside";


export default {
  name: "Layout",
  data() {
    return {
      flag:'',
    }
  },
  components:{
    Header,
    Aside,
  }
}
</script>

<style>
:root {
  --admin-primary: #2563eb;
  --admin-secondary: #0ea5e9;
  --admin-gradient: linear-gradient(135deg, #2563eb 0%, #0ea5e9 100%);
  --admin-bg: #f6f8fb;
  --admin-card-bg: rgba(255, 255, 255, 0.92);
  --admin-text-main: #111827;
  --admin-text-dim: #4b5563;
  --admin-text-muted: #6b7280;
  --admin-border: rgba(37, 99, 235, 0.12);
  --admin-shadow: 0 10px 30px rgba(37, 99, 235, 0.08);
}

body {
  background: var(--admin-bg);
  color: var(--admin-text-main);
}

.a-card {
  background: var(--admin-card-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--admin-border);
  border-radius: 16px;
  box-shadow: var(--admin-shadow);
}

.admin-title h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: var(--admin-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.text-dim { color: var(--admin-text-dim); }
.text-muted { color: var(--admin-text-muted); }
</style>

<style scoped>
.layout {
  min-height: 100vh;
  background: var(--admin-bg);
}

.content {
  margin-left: 200px;
  padding: 90px 24px 24px;
  width: calc(100% - 200px);
  box-sizing: border-box;
  min-width: 0;
}

@media (max-width: 1024px) {
  .content {
    margin-left: 200px;
    width: calc(100% - 200px);
    padding: 90px 16px 16px;
  }
}

@media (max-width: 768px) {
  .content {
    margin-left: 0;
    width: 100%;
    padding: 90px 12px 12px;
  }
}
</style>