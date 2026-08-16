package iwmvi.erp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth ->
                            auth.requestMatchers(
                                        "/login",
                                        "/privacidade",
                                        "/css/**",
                                        "/js/**",
                                        "/images/**")
                                    .permitAll()
                                    .requestMatchers("/uploads/funcionarios/**")
                                    .hasRole("ADMIN")
                                    .requestMatchers("/usuarios/**", "/auditoria/**", "/funcionarios/**")
                                    .hasRole("ADMIN")
                                    .anyRequest()
                                    .authenticated())
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
                .logout(
                        logout ->
                            logout
                                .logoutSuccessUrl("/login?logout")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll())
                .headers(
                        headers ->
                            headers
                                .contentSecurityPolicy(
                                        policy ->
                                            policy.policyDirectives(
                                                    "default-src 'self'; img-src 'self' data:; "
                                                        + "style-src 'self'; script-src 'self'; "
                                                        + "connect-src 'self'; form-action 'self'; "
                                                        + "frame-ancestors 'none'; base-uri 'self'"))
                                .referrerPolicy(
                                        policy ->
                                            policy.policy(
                                                    ReferrerPolicyHeaderWriter.ReferrerPolicy
                                                            .NO_REFERRER))
                                .frameOptions(frame -> frame.deny())
                                .httpStrictTransportSecurity(
                                        hsts ->
                                            hsts.includeSubDomains(true)
                                                .preload(true)
                                                .maxAgeInSeconds(31536000)));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
