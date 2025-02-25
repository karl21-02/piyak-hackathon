package com.example.backend.mail.controller;

import com.example.backend.mail.model.request.EmailRequest;
import com.example.backend.mail.model.request.EmailSendRequest;
import com.example.backend.mail.model.response.EmailResponse;
import com.example.backend.mail.service.ChatGptService;
import com.example.backend.mail.service.EmailService;
import com.example.backend.mail.service.GmailService;
import com.example.backend.mail.service.SendService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailController {

    private final SendService sendService;

    private final ChatGptService chatGptService;

    private final EmailService emailService;


    private final GmailService gmailService;

    // 모든 메일 응답 조회
    @GetMapping("/all")
    public ResponseEntity<List<EmailResponse>> getAllEmails() {
        List<EmailResponse> emails = emailService.getAllEmails();
        return ResponseEntity.ok(emails);
    }

    // 저장된 메일 응답 조회
    @GetMapping("/saved")
    public ResponseEntity<List<EmailResponse>> getSavedEmails() {
        List<EmailResponse> emails = emailService.getSavedEmails();
        return ResponseEntity.ok(emails);
    }

    // 전송된 메일 응답 조회
    @GetMapping("/sent")
    public ResponseEntity<List<EmailResponse>> getSentEmails() {
        List<EmailResponse> emails = emailService.getSentEmails();
        return ResponseEntity.ok(emails);
    }

    @PostMapping("/generate-mail")
    public ResponseEntity<EmailResponse> generateEmail(@RequestBody EmailRequest emailRequest) {
        EmailResponse response = chatGptService.generateEmail(emailRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/save-state")
    public void saveState(@RequestBody EmailResponse emailResponse) {
        emailService.updateState(emailResponse);
    }

    @GetMapping("/emails")
    public String getEmails(@RequestParam String accessToken) {

        return gmailService.getLatestEmails(accessToken);
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailSendRequest emailSendRequest) {
        try {

//             EmailService를 호출하여 이메일 전송
            sendService.sendEmail(
                    emailSendRequest.getAccessToken(),
                    emailSendRequest.getRecipient(),
                    emailSendRequest.getSubject(),
                    emailSendRequest.getBody()

            );

            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            // 에러 발생 시 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }
}