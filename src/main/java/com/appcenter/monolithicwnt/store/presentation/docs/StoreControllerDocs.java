package com.appcenter.monolithicwnt.store.presentation.docs;

import com.appcenter.monolithicwnt.auth.presentation.AuthenticationPrincipal;
import com.appcenter.monolithicwnt.global.dto.response.ErrorResponse;
import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import com.appcenter.monolithicwnt.store.dto.request.StoreCreateRequest;
import com.appcenter.monolithicwnt.store.dto.response.StoreResponse;
import com.appcenter.monolithicwnt.user.dto.request.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Store API", description = "매장 관련 API 명세")
public interface StoreControllerDocs {
    // 가게 생성
    @Operation(summary = "가게 생성", description = "가게을 생성합니다. 인증이 필요하며, 주소와 위치 좌표를 포함합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가게 생성 성공"),
            @ApiResponse(responseCode = "400", description = """
                    - [S-001] 유효하지 않은 매장 이름입니다.
                    - [S-002] 유효하지 않은 전화번호 형식입니다.
                    - [S-003] 유효하지 않은 인스타그램 계정입니다.
                    - [S-004] 주소 또는 좌표 정보가 잘못되었습니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = """
                    - [T-005] 인증이 필요합니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/create")
    ResponseEntity<SuccessResponse<?>> createStore(
            @RequestBody StoreCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Authentication authentication
    );

    @Operation(summary = "가게 상세 조회", description = "가게  ID를 통해 특정 가게의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 조회 성공"),
            @ApiResponse(responseCode = "404", description = """
                    - [S-005] 해당 가게를 찾을 수 없습니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{storeId}")
    ResponseEntity<SuccessResponse<StoreResponse>> getStores(
            @PathVariable("storeId") Long storeId
    );
}
