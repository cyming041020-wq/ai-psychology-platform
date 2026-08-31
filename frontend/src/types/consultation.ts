export interface ConsultationMessage {
  id: number
  role: 'USER' | 'ASSISTANT'
  content: string
  createdAt: string
}

export interface ConsultationSession {
  id: number
  sessionType: string
  status: string
  startedAt: string
  endedAt: string | null
  messages: ConsultationMessage[]
}

export interface SendMessageRequest {
  content: string
}
