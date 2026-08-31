<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ArrowLeft, Lock, User } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: '', password: '', displayName: '' })

const rules: FormRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { max: 64, message: '账号不能超过 64 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 128, message: '密码长度需为 8-128 个字符', trigger: 'blur' },
  ],
  displayName: [{ max: 64, message: '昵称不能超过 64 个字符', trigger: 'blur' }],
}

async function handleRegister() {
  if (!formRef.value || loading.value) {
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    await auth.register(form)
    ElMessage.success('注册成功，已自动登录')
    await router.push('/consultation')
  } catch (error: unknown) {
    const message = axios.isAxiosError(error) ? error.response?.data?.message : undefined
    ElMessage.error(message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-shell">
    <el-card class="auth-card" shadow="never">
      <el-button text :icon="ArrowLeft" @click="router.push('/home')">返回首页</el-button>
      <div class="auth-heading">
        <span class="brand-mark"><User /></span>
        <p class="eyebrow">创建账号</p>
        <h1>开始你的支持之旅</h1>
        <p>注册后即可创建属于自己的 AI 咨询会话。</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="auth-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item label="昵称（可选）" prop="displayName">
          <el-input v-model="form.displayName" :prefix-icon="User" autocomplete="nickname" placeholder="怎么称呼你" />
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" :prefix-icon="User" autocomplete="username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            :prefix-icon="Lock"
            type="password"
            autocomplete="new-password"
            show-password
            placeholder="至少 8 个字符"
            @keyup.enter="handleRegister"
          />
        </el-form-item>
        <el-button type="primary" size="large" class="full-width" :loading="loading" @click="handleRegister">
          注册并开始
        </el-button>
        <el-button text class="full-width auth-secondary-action" @click="router.push('/login')">
          已有账号，去登录
        </el-button>
      </el-form>
    </el-card>
  </main>
</template>
