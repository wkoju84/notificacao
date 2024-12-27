package com.william.notificacao.business;

import com.william.notificacao.business.dtos.TarefaDTO;
import com.william.notificacao.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${envio.email.remetente}")
    private String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void enviaEmail(TarefaDTO dto){

        try {
            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mensagem,
                    true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));
            mimeMessageHelper.setTo(InternetAddress.parse(dto.getEmailUsuario()));
            mimeMessageHelper.setSubject("Notificação de Tarefa");

            Context context = new Context();
            context.setVariable("nomeTarefa", dto.getNomeTarefa());
            context.setVariable("dataEvento", dto.getDataEvento());
            context.setVariable("descricao", dto.getDescricao());

            String template = templateEngine.process("notificacao", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(mensagem);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar email ", e.getCause());
        }

    }
}
