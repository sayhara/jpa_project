package com.jpaproject.controller;

import com.jpaproject.domain.Account;
import com.jpaproject.domain.Tag;
import com.jpaproject.domain.Zone;
import com.jpaproject.dto.SignUpForm;
import com.jpaproject.dto.TagForm;
import com.jpaproject.dto.ZoneForm;
import com.jpaproject.repository.AccountRepository;
import com.jpaproject.repository.TagRepository;
import com.jpaproject.repository.ZoneRepository;
import com.jpaproject.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ZoneRepository zoneRepository;

    private Zone testZone=Zone.builder().part1("?????????1").part2("?????????2").part3("?????????3").build();

    @BeforeEach
    void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("gyuwon");
        signUpForm.setEmail("google@google.com");
        signUpForm.setPassword("123456789");
        signUpForm.setPasswordRepeat("123456789");
        accountService.makeAccount(signUpForm);
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    /*
    ????????? ?????? ?????? gyuwon????????? ????????? ?????? ????????? ????????? ??????.
    @BeforeEach() & @WithUserDetails()??? ?????? ?????? ??????????????? ??????.

     */

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ???")
    @Test
    void updateProfileForm() throws Exception{
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? - ????????? ??????")
    @Test
    void updateProfile() throws Exception{
        String bio="????????? ????????? ???????????? ??????";
        mockMvc.perform(post("/settings/profile")
                .param("bio",bio)
                .with(csrf())) // FORM ???????????? ????????? csrf() ?????? ??????
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account gyuwon = accountRepository.findByNickname("gyuwon");
        assertEquals(bio,gyuwon.getBio());
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? - ????????? ??????")
    @Test
    void updateProfile_error() throws Exception{
        String bio="=======================????????? 35?????? ???????????? ??????" +
                "======================================================????????? 35?????? ???????????? ??????\" +\n" +
                "                \"===============================";
        mockMvc.perform(post("/settings/profile")
                .param("bio",bio)
                .with(csrf())) // FORM ???????????? ????????? csrf() ?????? ??????
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account gyuwon = accountRepository.findByNickname("gyuwon");
        assertNull(gyuwon.getBio());
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? ???")
    @Test
    void updatePasswordForm() throws Exception{
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? - ????????? ??????")
    @Test
    void updatePassword() throws Exception{
        String newPassword="11223344";
        String newPasswordRepeat="11223344";
        mockMvc.perform(post("/settings/password")
                    .param("newPassword",newPassword)
                    .param("newPasswordRepeat",newPasswordRepeat)
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/settings/password"))
                    .andExpect(flash().attributeExists("message"));

        Account gyuwon = accountRepository.findByNickname("gyuwon");
        assertTrue(passwordEncoder.matches(newPassword,gyuwon.getPassword()));
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? - ????????? ??????")
    @Test
    void updatePassword_error() throws Exception{
        String newPassword="11223344";
        String newPasswordRepeat="22334455";
        mockMvc.perform(post("/settings/password")
                .param("newPasword",newPassword)
                .param("newPasswordRepeat",newPasswordRepeat)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(status().is2xxSuccessful()); // 200?????? ??????

    }

    // ???????????? ?????????
    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ?????? ???")
    @Test
    void updateTagsForm() throws Exception{
        mockMvc.perform(get("/settings/tags"))
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ??????")
    @Test
    void addTag() throws Exception{

        TagForm tagForm=new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm)) // ???????????? ??????????????? ?????? ???????????? ?????????
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
//        assertTrue(accountRepository.findByNickname("gyuwon").getTags().contains(newTag));
//        accountRepository.findByNickname("gyuwon")????????? ?????????????????? ?????? @Transactional?????? ??????
//        ????????? ????????? ?????? ????????? detached -> no Session????????? ???
//        ?????????, ??? ??????????????? ?????? @Transactional??? ????????? persist ????????? ??????
        assertTrue(accountRepository.findByNickname("gyuwon").getTags().contains(newTag));
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ??????")
    @Test
    void removeTag() throws Exception{
        Account gyuwon = accountRepository.findByNickname("gyuwon");
        Tag newTag=tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(gyuwon,newTag);

        assertTrue(gyuwon.getTags().contains(newTag));

        TagForm tagForm=new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(gyuwon.getTags().contains(newTag));
    }
    
    //???????????? ?????????
    @WithUserDetails(value="gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? ?????? ???")
    @Test
    void updateZoneForm() throws Exception{
        mockMvc.perform(get("/settings/zones"))
                .andExpect(view().name("settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? ??????")
    @Test
    void addZone() throws Exception{

        ZoneForm zoneForm=new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post("/settings/zones/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Zone newZone=zoneRepository.findByPart1AndPart3(testZone.getPart1(),testZone.getPart3());
        assertNotNull(newZone);

        assertTrue(accountRepository.findByNickname("gyuwon").getZones().contains(newZone));
    }

    @WithUserDetails(value = "gyuwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? ??????")
    @Test
    void removeZone() throws Exception{

        Account gyuwon=accountRepository.findByNickname("gyuwon");
        Zone zone = zoneRepository.findByPart1AndPart3(testZone.getPart1(), testZone.getPart3());
        accountService.addZone(gyuwon,zone);

        assertTrue(gyuwon.getZones().contains(zone));

        ZoneForm zoneForm=new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post("/settings/zones/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(gyuwon.getZones().contains(zone));
    }


}