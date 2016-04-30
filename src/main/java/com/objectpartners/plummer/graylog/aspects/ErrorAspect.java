package com.objectpartners.plummer.graylog.aspects;

import com.objectpartners.plummer.graylog.graylog.GelfMessage;
import com.objectpartners.plummer.graylog.graylog.GraylogRestInterface;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Aspect
@Named
public class ErrorAspect {

    @Inject
    protected GraylogRestInterface graylog;

    @AfterThrowing(pointcut = "execution(* com.objectpartners.plummer.graylog.jobs..*(..))", throwing = "e")
    public void errorThrown(Throwable e) throws IOException {
        GelfMessage message = new GelfMessage();
        message.setShortMessage("Error");
        message.setFullMessage(e.getMessage());

        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(printWriter);
            message.getAdditionalProperties().put("stacktrace", stringWriter.toString());
        }

        graylog.logEvent(message);
    }
}
