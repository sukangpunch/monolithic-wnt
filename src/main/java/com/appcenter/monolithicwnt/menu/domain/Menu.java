package com.appcenter.monolithicwnt.menu.domain;

import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.store.domain.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    private static final int MAX_DETAIL_LENGTH = 30;
    private static final int MIN_DETAIL_LENGTH = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private boolean representative;

    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "menu_details",
            joinColumns = @JoinColumn(name = "menu_id")
    )
    @Column(name = "detail", nullable = false)
    private List<String> details;

    public Menu(
            String name,
            int price,
            boolean representative,
            Store store,
            List<String> details
    ) {
        validate(name, price, details);
        this.name = name;
        this.price = price;
        this.representative = representative;
        this.store = store;
        this.details = details;
    }

    private void validate(String name, int price, List<String> details){
        validateName(name);
        validatePrice(price);
        validateDetails(details);
    }

    private void validateName(String name) {
        if(name == null || name.isBlank()){
            throw new BusinessException(ErrorCode.MENU_NAME_INVALID);
        }
    }

    private void validatePrice(int price) {
        if(price > 0 && price < 1000000){
            throw new BusinessException(ErrorCode.MENU_PRICE_INVALID);
        }
    }

    private void validateDetails(List<String> details) {
        if(details == null || details.isEmpty()){
            return; // 설명 없어도 괜춘
        }

        for(String detail : details){
            if(detail == null || detail.isBlank()){
                throw new BusinessException(ErrorCode.MENU_DETAILS_INVALID);
            }

            if(detail.length() > MAX_DETAIL_LENGTH || detail.length() < MIN_DETAIL_LENGTH){
                throw new BusinessException(ErrorCode.MENU_DETAIL_LENGTH_INVALID);
            }
        }
    }
}
