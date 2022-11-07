package online.contactraphael.readabook.controller;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.model.user.ShortUser;
import online.contactraphael.readabook.service.service.AppUserService;
import online.contactraphael.readabook.utility.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping(path = "/all{page}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> getAll(@PathVariable Integer page) {
        List<ShortUser> users = appUserService.getAll(page);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("responseBody", users)));

    }

    @GetMapping(path = "/get/{email}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> getOne(@PathVariable String email) {
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("responseBody", appUserService.findByEmail(email))));
    }

    @DeleteMapping(path = "/delete/{email}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> deleteUserAccount(@PathVariable String email) {
        appUserService.deleteUser(email);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @DeleteMapping(path = "/suspend/{email}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage> suspendUserAccount(@PathVariable String email) {
        appUserService.suspendUser(email);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }
}
