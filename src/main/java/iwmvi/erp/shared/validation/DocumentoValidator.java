package iwmvi.erp.shared.validation;

import java.util.Locale;

public final class DocumentoValidator {

    private static final int[] PESOS_CNPJ_PRIMEIRO = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CNPJ_SEGUNDO = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private DocumentoValidator() {
    }

    public static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    public static String normalizarDocumento(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }

    public static boolean cpfValido(String cpf) {
        String digits = somenteDigitos(cpf);
        if (digits.length() != 11 || todosIguais(digits)) {
            return false;
        }

        return calcularDigitoCpf(digits.substring(0, 9), 10) == digits.charAt(9) - '0'
            && calcularDigitoCpf(digits.substring(0, 10), 11) == digits.charAt(10) - '0';
    }

    public static boolean cnpjValido(String cnpj) {
        String normalizado = normalizarDocumento(cnpj);
        if (!normalizado.matches("[A-Z0-9]{12}[0-9]{2}") || todosIguais(normalizado)) {
            return false;
        }

        int primeiro = digitoCnpj(normalizado.substring(0, 12), PESOS_CNPJ_PRIMEIRO);
        int segundo = digitoCnpj(normalizado.substring(0, 12) + primeiro, PESOS_CNPJ_SEGUNDO);
        return primeiro == normalizado.charAt(12) - '0' && segundo == normalizado.charAt(13) - '0';
    }

    public static boolean documentoEhCpf(String documento) {
        String normalizado = normalizarDocumento(documento);
        return normalizado.matches("\\d{11}");
    }

    public static boolean documentoEhCnpj(String documento) {
        String normalizado = normalizarDocumento(documento);
        return normalizado.matches("[A-Z0-9]{12}[0-9]{2}");
    }

    public static boolean gtinValido(String gtin) {
        String digits = somenteDigitos(gtin);
        if (!(digits.length() == 8
            || digits.length() == 12
            || digits.length() == 13
            || digits.length() == 14)) {
            return false;
        }

        int soma = 0;
        boolean pesoTres = true;
        for (int i = digits.length() - 2; i >= 0; i--) {
            soma += (digits.charAt(i) - '0') * (pesoTres ? 3 : 1);
            pesoTres = !pesoTres;
        }
        int digito = (10 - (soma % 10)) % 10;
        return digito == digits.charAt(digits.length() - 1) - '0';
    }

    private static int calcularDigitoCpf(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = 11 - (soma % 11);
        return resto >= 10 ? 0 : resto;
    }

    private static int digitoCnpj(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            int valor = base.charAt(i) - '0';
            soma += valor * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean todosIguais(String valor) {
        return valor.chars().allMatch(c -> c == valor.charAt(0));
    }
}
