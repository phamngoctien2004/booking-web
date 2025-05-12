package com.datsan.caulong.dto.request;

import com.datsan.caulong.util.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatches // kiểm tra mật khẩu
public class RegisterRequest {

    @Email(message = "Email không đúng định dạng")
    @NotBlank
    private String email;

    @NotBlank
    @Min(value=6, message = "Mật khẩu lớn hơn 6 kí tự")
    private String password;

    private String confirmPassword;

}
