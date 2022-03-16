package ch.uzh.ifi.sopra22.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Operation(summary = "Get user with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Integer. class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @GetMapping(value = "/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public int GETUser(@Parameter(description = "UserID") @PathVariable Long id) {
        return 100;
    }
}