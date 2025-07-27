package com.appcenter.monolithicwnt.user.domain;

import com.appcenter.monolithicwnt.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) // 특지어 필드만 비교 대상에 포함
@ToString
public class User extends BaseEntity {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    public User(String email, String password, String nickname){
        validate(email, password, nickname);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    private void validate(String email, String password, String nickname){
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
    }

    private void validateEmail(String email){
        if(email == null || email.isBlank() || !email.matches(EMAIL_REGEX)){
            throw new RuntimeException();
        }
    }

    private void validatePassword(String password){
        if(password == null || password.isBlank()){
            throw new RuntimeException();
        }
    }

    private void validateNickname(String nickname){
        if(nickname == null || nickname.isBlank()){
            throw new RuntimeException();
        }
    }

    public boolean checkPassword(String loginPassword){
        return password.equals(loginPassword);
    }

}
