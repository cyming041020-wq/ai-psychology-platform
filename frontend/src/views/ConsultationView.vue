<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ChatDotRound, Plus, Promotion, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

import {
  createConsultationSession,
  getConsultationSession,
  listConsultationSessions,
  sendConsultationMessage,
} from '../services/consultation'
import { useAuthStore } from '../stores/auth'
import type { ConsultationSession } from '../types/consultation'

const router = useRouter()
const auth = useAuthStore()
const sessions = ref<ConsultationSession[]>([])
const activeSession = ref<ConsultationSession | null>(null)
const draft = ref('')
const loading = ref(true)
const sending = ref(false)
const displayName = computed(() => auth.user?.displayName || auth.user?.username || '')

function formatDate(value: string) {
  return new Date(value).toLocaleString('zh-CN', {
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function errorMessage(error: unknown, fallback: string) {
  return axios.isAxiosError(error) ? error.response?.data?.message || fallback : fallback
}

function responseStatus(error: unknown) {
  return axios.isAxiosError(error) ? error.response?.status : undefined
}

async function selectSession(sessionId: number) {
  try {
    activeSession.value = await getConsultationSession(sessionId)
  } catch (error: unknown) {
    if (responseStatus(error) === 404) {
      activeSession.value = null
      ElMessage.warning('会话已失效，请新建一个会话')
    } else if (responseStatus(error) !== 401) {
      ElMessage.error(errorMessage(error, '无法加载咨询会话'))
    }
  }
}

async function loadSessions() {
  loading.value = true
  try {
    sessions.value = await listConsultationSessions()
    if (sessions.value.length > 0) {
      await selectSession(activeSession.value?.id || sessions.value[0].id)
    }
  } catch (error: unknown) {
    if (responseStatus(error) !== 401) {
      ElMessage.error(errorMessage(error, '无法加载咨询会话'))
    }
  } finally {
    loading.value = false
  }
}

async function startSession() {
  try {
    const session = await createConsultationSession()
    sessions.value = [session, ...sessions.value]
    activeSession.value = session
  } catch (error: unknown) {
    if (responseStatus(error) !== 401) {
      ElMessage.error(errorMessage(error, '创建会话失败'))
    }
  }
}

async function sendMessage() {
  const content = draft.value.trim()
  if (!content || sending.value) {
    return
  }

  if (!activeSession.value) {
    await startSession()
  }
  if (!activeSession.value) {
    return
  }

  sending.value = true
  draft.value = ''
  try {
    const session = await sendConsultationMessage(activeSession.value.id, { content })
    activeSession.value = session
    sessions.value = sessions.value.map((item) => item.id === session.id ? session : item)
  } catch (error: unknown) {
    draft.value = content
    if (responseStatus(error) === 404) {
      activeSession.value = null
      ElMessage.warning('当前会话已失效，请新建一个会话后重试')
      await loadSessions()
    } else if (responseStatus(error) !== 401) {
      ElMessage.error(errorMessage(error, '消息发送失败'))
    }
  } finally {
    sending.value = false
  }
}

function logout() {
  auth.logout()
  router.push('/home')
}

onMounted(loadSessions)
</script>

<template>
  <main class="app-shell consultation-shell">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark"><ChatDotRound /></span>
        <span>心理支持平台</span>
      </div>
      <div class="header-actions">
        <span class="user-greeting"><User /> {{ displayName }}</span>
        <el-button plain @click="logout">退出</el-button>
      </div>
    </header>

    <section class="consultation-layout">
      <aside class="session-sidebar">
        <div class="session-sidebar-heading">
          <div>
            <p class="panel-label">我的咨询</p>
            <h2>会话记录</h2>
          </div>
          <el-button type="primary" :icon="Plus" circle aria-label="新建会话" @click="startSession" />
        </div>
        <div v-if="loading" class="session-loading">加载中...</div>
        <el-empty v-else-if="sessions.length === 0" description="还没有会话" :image-size="80" />
        <div v-else class="session-list">
          <button
            v-for="session in sessions"
            :key="session.id"
            class="session-item"
            :class="{ active: activeSession?.id === session.id }"
            type="button"
            @click="selectSession(session.id)"
          >
            <span class="session-item-icon"><ChatDotRound /></span>
            <span class="session-item-copy">
              <strong>AI 咨询</strong>
              <small>{{ formatDate(session.startedAt) }}</small>
            </span>
          </button>
        </div>
      </aside>

      <section class="chat-panel">
        <template v-if="activeSession">
          <div class="chat-heading">
            <div>
              <p class="eyebrow">安全、私密的倾听空间</p>
              <h1>今天感觉怎么样？</h1>
            </div>
            <el-tag type="success" effect="plain">会话进行中</el-tag>
          </div>
          <div class="chat-messages" aria-live="polite">
            <el-empty v-if="activeSession.messages.length === 0" description="从分享此刻的感受开始吧" />
            <div
              v-for="message in activeSession.messages"
              :key="message.id"
              class="message-row"
              :class="message.role === 'USER' ? 'from-user' : 'from-assistant'"
            >
              <div class="message-bubble">
                <p>{{ message.content }}</p>
                <time>{{ formatDate(message.createdAt) }}</time>
              </div>
            </div>
          </div>
          <div class="chat-composer">
            <el-input
              v-model="draft"
              type="textarea"
              :rows="3"
              maxlength="2000"
              show-word-limit
              resize="none"
              placeholder="写下你此刻想分享的事..."
              @keydown.enter.exact.prevent="sendMessage"
            />
            <div class="composer-footer">
              <span>AI 回复为演示服务，不能替代专业医疗建议。</span>
              <el-button type="primary" :icon="Promotion" :loading="sending" @click="sendMessage">
                发送
              </el-button>
            </div>
          </div>
        </template>
        <div v-else class="chat-empty">
          <span class="brand-mark"><ChatDotRound /></span>
          <h1>准备好开始倾诉了吗？</h1>
          <p>创建一个咨询会话，把此刻的感受写下来。</p>
          <el-button type="primary" :icon="Plus" @click="startSession">新建咨询会话</el-button>
        </div>
      </section>
    </section>
  </main>
</template>
