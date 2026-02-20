<template>
  <div class="member-layout">
    <!-- 星空背景 -->
    <div class="bg-overlay"></div>

    <div class="container">
      <!-- 头部 Logo & 用户信息 -->
     <MemberHeader />

      <!-- 主体区域 -->
      <div class="member-body glass">
        <!-- 侧边栏 -->
        <MemberAside class="member-aside" />
        <!-- 内容 -->
        <main class="content">
         <router-view />
        </main>
     </div>
   </div>
  </div>
</template>

<script>
import MemberHeader from "@/components/member/MemberHeader";
import MemberAside from "@/components/member/MemberAside";

export default {
  name: "MemberLayout",
  components: { MemberHeader, MemberAside },
};
</script>

<style>
@import url("https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap");

:root {
  --member-primary: #667eea;
  --member-secondary: #764ba2;
  --member-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  --member-bg: #f6f8fb;
  --member-card-bg: rgba(255, 255, 255, 0.9);
  --member-text-main: #1f2937;
  --member-text-dim: #4b5563;
  --member-text-muted: #6b7280;
  --member-border: rgba(102, 126, 234, 0.1);
  --card-shadow: 0 10px 30px rgba(102, 126, 234, 0.08);
}

body { 
  margin: 0; 
  font-family: "Poppins", -apple-system, sans-serif; 
  background: var(--member-bg); 
  color: var(--member-text-main); 
  overflow-x: hidden; 
}

/* 统一浅色现代卡片 */
.m-card {
  background: var(--member-card-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--member-border);
  border-radius: 20px;
  box-shadow: var(--card-shadow);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 24px;
}

.m-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.12);
  border-color: rgba(102, 126, 234, 0.3);
}

/* 页面标题通用样式 */
.page-title-section {
  margin-bottom: 24px;
}

.page-title-section h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: var(--member-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Element UI 浅色适配重置 */
.el-table {
  background-color: transparent !important;
  color: var(--member-text-main) !important;
}

.el-table th {
  background: rgba(102, 126, 234, 0.05) !important;
  color: var(--member-primary) !important;
  font-weight: 600;
  border: none !important;
}

.el-table td {
  border-bottom: 1px solid rgba(0, 0, 0, 0.03) !important;
}

.text-dim { color: var(--member-text-dim); }
.text-muted { color: var(--member-text-muted); }

.bg-overlay { 
  position: fixed; 
  top: 0; 
  left: 0; 
  width: 100%; 
  height: 100%; 
  z-index: -1; 
  background: 
    radial-gradient(circle at 10% 10%, rgba(102, 126, 234, 0.05) 0%, transparent 30%),
    radial-gradient(circle at 90% 90%, rgba(118, 75, 162, 0.05) 0%, transparent 30%),
    #f6f8fb;
}
</style>

<style scoped>
.container {
  max-width: 1300px;
  margin: 0 auto;
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  box-sizing: border-box;
}

.member-body {
  flex: 1;
  display: flex;
  gap: 30px;
  margin-top: 30px;
  min-height: 0; /* 允许 flex 子项缩放 */
}

.member-aside {
  width: 260px;
  flex-shrink: 0;
  position: sticky;
  top: 30px;
  height: fit-content;
}

.content {
  flex: 1;
  min-width: 0;
  padding-bottom: 40px;
}

@media (max-width: 1024px) {
  .member-aside {
    width: 220px;
  }
}

@media (max-width: 768px) {
  .member-body {
    flex-direction: column;
  }
  .member-aside {
    width: 100%;
    position: static;
  }
}
</style>
