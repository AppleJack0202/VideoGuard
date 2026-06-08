<template>
  <section class="panel">
    <el-form label-width="72px" style="max-width: 360px">
      <el-form-item label="用户名">
        <el-input placeholder="admin / reviewer / user" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" placeholder="开发阶段占位" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary">登录</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

