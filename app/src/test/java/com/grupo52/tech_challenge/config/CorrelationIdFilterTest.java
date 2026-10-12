package com.grupo52.tech_challenge.config;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void propagaCorrelationIdRecebido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-correlationid", "abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> noMdc = new AtomicReference<>();

        filter.doFilter(request, response, new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                noMdc.set(MDC.get("correlationId"));
            }
        });

        assertThat(noMdc.get()).isEqualTo("abc-123");
        assertThat(response.getHeader("x-correlationid")).isEqualTo("abc-123");
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void geraCorrelationIdQuandoAusente() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());

        assertThat(response.getHeader("x-correlationid")).isNotBlank();
    }
}
