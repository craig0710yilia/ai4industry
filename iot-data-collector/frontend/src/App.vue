<template>
  <el-container style="height: 100vh;">
    <el-aside width="220px" style="background: #304156; overflow: hidden;">
      <div class="logo">
        <el-icon size="24" color="#fff"><Monitor /></el-icon>
        <span>IoT Collector</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/data-sources">
          <el-icon><Connection /></el-icon>
          <span>Data Sources</span>
        </el-menu-item>
        <el-menu-item index="/monitor">
          <el-icon><DataLine /></el-icon>
          <span>Monitor</span>
        </el-menu-item>
        <el-menu-item index="/backups">
          <el-icon><FolderOpened /></el-icon>
          <span>Backups</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; padding: 0 20px;">
        <h3 style="margin: 0; color: #304156;">Industrial IoT Data Collection System</h3>
      </el-header>
      <el-main style="background: #f0f2f5; padding: 20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; }
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #3d4f61;
}
</style>
