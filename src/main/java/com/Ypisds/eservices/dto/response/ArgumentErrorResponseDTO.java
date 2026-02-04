package com.Ypisds.eservices.dto.response;

import java.util.List;

public record ArgumentErrorResponseDTO(String message,
                                       List<ArgumentFieldErrorDTO> fieldErrors) {
}
