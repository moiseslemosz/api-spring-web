package dio.web.api.handler;

import dio.web.api.exception.BusinessException;

public class CampoObrigatorioException extends BusinessException {
    public CampoObrigatorioException(String campo) {
        super("O campo '%s' é obrigatório.", campo);
    }

}
