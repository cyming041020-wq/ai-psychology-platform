<script setup lang="ts">
import { User, Lock, ArrowLeft } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <main class="auth-shell">
    <el-card class="auth-card" shadow="never">
      <el-button text :icon="ArrowLeft" @click="router.push('/home')">返回首页</el-button>
      <div class="auth-heading">
        <span class="brand-mark"><User /></span>
        <p class="eyebrow">安全登录</p>
        <h1>欢迎回来</h1>
        <p>登录后继续使用心理支持服务。</p>
      </div>
      <el-form label-position="top" class="auth-form">
        <el-form-item label="账号">
          <el-input :prefix-icon="User" placeholder="请输入手机号或邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input :prefix-icon="Lock" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" size="large" class="full-width">登录</el-button>
      </el-form>
    </el-card>
  </main>
</template>
