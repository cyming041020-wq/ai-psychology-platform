<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, CircleCheck, FirstAidKit, User } from '@element-plus/icons-vue'

const router = useRouter()
const connected = ref(false)

async function checkBackend() {
  try {
    const response = await fetch('/api/test')
    connected.value = response.ok
  } catch {
    connected.value = false
  }
}
</script>

<template>
  <main class="app-shell">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark"><FirstAidKit /></span>
        <span>心理支持平台</span>
      </div>
      <el-button :icon="User" plain @click="router.push('/login')">登录</el-button>
    </header>

    <section class="hero">
      <p class="eyebrow">AI 辅助 · 专业守护 · 随时可用</p>
      <h1>让每一次倾听，都有温度与回应</h1>
      <p class="hero-copy">从情绪记录到专业咨询，为你提供可靠、私密的心理支持入口。</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" :icon="ChatDotRound">开始倾诉</el-button>
        <el-button size="large" :icon="FirstAidKit">危机援助</el-button>
      </div>
    </section>

    <section class="status-panel" aria-live="polite">
      <div>
        <p class="panel-label">系统状态</p>
        <p class="panel-title">基础服务连接检查</p>
      </div>
      <div class="status-action">
        <span class="status-dot" :class="{ online: connected }"></span>
        <span>{{ connected ? '后端服务正常' : '尚未检查' }}</span>
        <el-button text :icon="CircleCheck" @click="checkBackend">检查连接</el-button>
      </div>
    </section>
  </main>
</template>
