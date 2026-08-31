package com.psychology.consultation.service;

import org.springframework.stereotype.Service;

@Service
public class MockAiReplyService implements AiReplyService {

    @Override
    public String reply(String message) {
        String reply = "谢谢你愿意分享这些。你刚才提到“" + message.trim()
                + "”。我们可以先把注意力放回当下：慢慢呼吸，并想一想此刻最需要的支持是什么。";
        return reply.length() <= 2000 ? reply : reply.substring(0, 2000);
    }
}
