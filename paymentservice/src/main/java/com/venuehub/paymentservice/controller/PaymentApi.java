package com.venuehub.paymentservice.controller;
import com.stripe.exception.StripeException;
import com.venuehub.paymentservice.dto.BookingIdDto;
import com.venuehub.paymentservice.model.BookingOrder;
import com.venuehub.paymentservice.response.BookingOrderListResponse;
import com.venuehub.paymentservice.response.ConfirmPaymentResponse;
import com.venuehub.paymentservice.response.CreatePaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

public interface PaymentApi {

    @Operation(summary = "Create Payment Intent", description = "Creates a payment intent for a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created: Payment intent created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreatePaymentResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized to perform this action"),
            @ApiResponse(responseCode = "404", description = "Not Found: Booking with the provided ID not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: Unexpected error occurred")
    })
    ResponseEntity<CreatePaymentResponse> createPaymentIntent(
            @RequestBody @Valid BookingIdDto bookingIdDto,
            @AuthenticationPrincipal Jwt jwt) throws StripeException;

    @Operation(summary = "Get Order Status", description = "Retrieve the status of a booking order by order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Successfully retrieved order status",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingOrder.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized to perform this action"),
            @ApiResponse(responseCode = "404", description = "Not Found: Order with the provided ID not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: Unexpected error occurred")
    })
    ResponseEntity<BookingOrder> getOrderStatus(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Get User Orders", description = "Retrieve all orders placed by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Successfully retrieved user orders",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingOrderListResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized to perform this action"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: Unexpected error occurred")
    })
    ResponseEntity<BookingOrderListResponse> getUserOrders(
            @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Get Vendor Orders", description = "Retrieve all orders associated with the authenticated vendor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Successfully retrieved vendor orders",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingOrderListResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized to perform this action"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: Unexpected error occurred")
    })
    ResponseEntity<BookingOrderListResponse> getVendorOrders(
            @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Confirm Payment", description = "Confirm the payment status for a booking order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK: Payment confirmed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConfirmPaymentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid client or client secret provided"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized to perform this action"),
            @ApiResponse(responseCode = "404", description = "Not Found: Order with the provided client secret not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: Unexpected error occurred")
    })
    ConfirmPaymentResponse confirmPayment(
            @RequestParam("clientId") String clientId,
            @RequestParam("clientSecret") String clientSecret,
            @RequestParam("vendor") String vendor,
            @AuthenticationPrincipal Jwt jwt) throws Exception;
}
