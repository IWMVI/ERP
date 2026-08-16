package iwmvi.erp.compra;

import jakarta.validation.constraints.NotNull;

public record CriarPedidoCompraRequest(@NotNull Long fornecedorId) {}
