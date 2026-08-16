package iwmvi.erp.shared.validation;

public final class DocumentoValidator {

    private DocumentoValidator() {}

    public static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    public static boolean cpfValido(String cpf) {
        String digits = somenteDigitos(cpf);
        if (digits.length() != 11 || todosIguais(digits)) {
            return false;
        }

        return calcularDigito(digits.substring(0, 9), 10) == digits.charAt(9) - '0'
                && calcularDigito(digits.substring(0, 10), 11) == digits.charAt(10) - '0';
    }

    public static boolean cnpjValido(String cnpj) {
        String digits = somenteDigitos(cnpj);
        if (digits.length() != 14 || todosIguais(digits)) {
            return false;
        }

        int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return digitoModulo11(digits.substring(0, 12), pesosPrimeiro) == digits.charAt(12) - '0'
                && digitoModulo11(digits.substring(0, 13), pesosSegundo) == digits.charAt(13) - '0';
    }

    public static boolean gtinValido(String gtin) {
        String digits = somenteDigitos(gtin);
        if (!(digits.length() == 8 || digits.length() == 12 || digits.length() == 13 || digits.length() == 14)) {
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

    private static int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = 11 - (soma % 11);
        return resto >= 10 ? 0 : resto;
    }

    private static int digitoModulo11(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean todosIguais(String valor) {
        return valor.chars().allMatch(c -> c == valor.charAt(0));
    }
}
