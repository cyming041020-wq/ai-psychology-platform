import api from './api'
import type { ConsultationSession, SendMessageRequest } from '../types/consultation'

export async function listConsultationSessions() {
  const { data } = await api.get<ConsultationSession[]>('/consultations')
  return data
}

export async function createConsultationSession() {
  const { data } = await api.post<ConsultationSession>('/consultations')
  return data
}

export async function getConsultationSession(sessionId: number) {
  const { data } = await api.get<ConsultationSession>(`/consultations/${sessionId}`)
  return data
}

export async function sendConsultationMessage(sessionId: number, request: SendMessageRequest) {
  const { data } = await api.post<ConsultationSession>(
    `/consultations/${sessionId}/messages`,
    request,
  )
  return data
}
