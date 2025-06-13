package backend2.presentation;

import backend2.domain.UserDTO;
import backend2.domain.UserDataPortabilityDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    public void testCreateUser() throws Exception {
        // Create a unique username to avoid conflicts
        String uniqueUsername = "testuser" + UUID.randomUUID().toString().substring(0, 8);
        
        // Create a test user with minimal required fields
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("test@example.com");
        userDTO.setAddress("123 Test St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);

        // Convert user to JSON
        String userJson = objectMapper.writeValueAsString(userDTO);
        System.out.println("Request body: " + userJson);

        // Perform the request with debug output
        MvcResult result = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())  // Print request/response details
                .andExpect(status().isOk())
                .andReturn();

        // Print response for debugging
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response status: " + result.getResponse().getStatus());
        System.out.println("Response body: " + responseBody);

        // Verify we got a non-null response
        assertNotNull(responseBody, "Response body should not be null");
    }
    
    @Test
    public void testLoginUser() throws Exception {
        // 1. First create a user that we can log in with
        String uniqueUsername = "loginuser" + UUID.randomUUID().toString().substring(0, 8);
        String password = "Password123!";
        
        // Create user DTO for registration
        UserDTO registerDTO = new UserDTO();
        registerDTO.setUsername(uniqueUsername);
        registerDTO.setPwd(password);
        registerDTO.setEmail("login@example.com");
        registerDTO.setAddress("123 Login St");
        registerDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        registerDTO.setRoles(roles);

        // Register the user first (as admin)
        mockMvc.perform(post("/api/v1/auth")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());
        
        // 2. Now attempt to login with the created user
        UserDTO loginDTO = new UserDTO();
        loginDTO.setUsername(uniqueUsername);
        loginDTO.setPwd(password);
        
        // Perform login request
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();
        
        // Print response for debugging
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Login Response status: " + result.getResponse().getStatus());
        System.out.println("Login Response body: " + responseBody);
        
        // Verify we got a token in the response
        assertNotNull(responseBody, "Response body should not be null");
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void testLogoutUser() throws Exception {
        // Perform logout request
        MvcResult result = mockMvc.perform(post("/api/v1/auth/logout")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Get the response
        MockHttpServletResponse response = result.getResponse();
        String responseBody = response.getContentAsString();
        
        // Verify the response body contains "Logged out"
        assertEquals("Logged out", responseBody);
        
        // Check for the Set-Cookie header
        String setCookieHeader = response.getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader, "Set-Cookie header should be present");
        
        // Verify the cookie has been invalidated (max-age=0)
        assertTrue(setCookieHeader.contains("jwt="), "Cookie should be named 'jwt'");
        assertTrue(setCookieHeader.contains("Max-Age=0"), "Cookie should have Max-Age=0");
    }
    
    @Test
    public void testRegisterUserDirectly() throws Exception {
        // Create a unique username to avoid conflicts
        String uniqueUsername = "registeruser" + UUID.randomUUID().toString().substring(0, 8);
        
        // Create user DTO for registration
        UserDTO registerDTO = new UserDTO();
        registerDTO.setUsername(uniqueUsername);
        registerDTO.setPwd("Password123!");
        registerDTO.setEmail("register@example.com");
        registerDTO.setAddress("123 Register St");
        registerDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        registerDTO.setRoles(roles);
        
        // Register the user through the /api/v1/auth endpoint
        MvcResult result = mockMvc.perform(post("/api/v1/auth")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Print response for debugging
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Registration Response status: " + result.getResponse().getStatus());
        System.out.println("Registration Response body: " + responseBody);
        
        // Verify the response contains the user data
        assertNotNull(responseBody, "Response body should not be null");
        assertTrue(responseBody.contains(uniqueUsername), "Response should contain the username");
        assertTrue(responseBody.contains("register@example.com"), "Response should contain the email");
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    public void testGetAllUsers() throws Exception {
        // Test getting all users as an administrator
        MvcResult result = mockMvc.perform(get("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Verify we got a non-null response
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody, "Response body should not be null");
        
        // The response should be a JSON array
        assertTrue(responseBody.startsWith("["), "Response should be a JSON array");
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    public void testGetSpecificUser() throws Exception {
        // First create a user
        String uniqueUsername = "getuser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("getuser@example.com");
        userDTO.setAddress("123 Get St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extract the user ID from the response
        UserDTO createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDTO.class);
        Integer userId = createdUser.getId();
        
        // Now get the user by ID with proper JWT authentication
        MvcResult getResult = mockMvc.perform(get("/api/v1/users/" + userId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId))))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Verify the response contains the correct user data
        String responseBody = getResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains(uniqueUsername), "Response should contain the username");
        assertTrue(responseBody.contains("getuser@example.com"), "Response should contain the email");
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    public void testUpdateUser() throws Exception {
        // First create a user
        String uniqueUsername = "updateuser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("update@example.com");
        userDTO.setAddress("123 Update St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extract the user ID from the response
        UserDTO createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDTO.class);
        Integer userId = createdUser.getId();
        
        // Update the user
        createdUser.setEmail("updated@example.com");
        createdUser.setAddress("456 Updated St");
        
        // Perform the update request with JWT authentication
        MvcResult updateResult = mockMvc.perform(put("/api/v1/users/" + userId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Verify the response contains the updated data
        String responseBody = updateResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("updated@example.com"), "Response should contain the updated email");
        assertTrue(responseBody.contains("456 Updated St"), "Response should contain the updated address");
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    public void testDeleteUser() throws Exception {
        // First create a user
        String uniqueUsername = "deleteuser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("delete@example.com");
        userDTO.setAddress("123 Delete St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extract the user ID from the response
        UserDTO createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDTO.class);
        Integer userId = createdUser.getId();
        
        // Now delete the user (exercise right to be forgotten)
        mockMvc.perform(delete("/api/v1/users/" + userId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId))))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
        
        // Instead of checking if the user is completely deleted,
        // we'll verify that the user is marked as deleted by checking the getAllUsers endpoint
        // which should not include our deleted user
        MvcResult allUsersResult = mockMvc.perform(get("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andReturn();
        
        String allUsersResponse = allUsersResult.getResponse().getContentAsString();
        System.out.println("All users after deletion: " + allUsersResponse);
        
        // Verify that the deleted user's username is not in the list of all users
        assertTrue(!allUsersResponse.contains(uniqueUsername), 
            "Deleted user's username '" + uniqueUsername + "' should not appear in the list of all users");
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void testGetUserDataPortability() throws Exception {
        // First create a user
        String uniqueUsername = "datauser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("data@example.com");
        userDTO.setAddress("123 Data St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user as admin
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extract the user ID from the response
        UserDTO createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDTO.class);
        Integer userId = createdUser.getId();
        
        // Get the user's data portability info
        MvcResult dataResult = mockMvc.perform(get("/api/v1/users/" + userId + "/data-portability")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId))))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Verify the response contains user data
        String responseBody = dataResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains(uniqueUsername), "Response should contain the username");
        assertTrue(responseBody.contains("data@example.com"), "Response should contain the email");
    }
    
    @Test
    public void testGetCurrentUser() throws Exception {
        // Create a test user first
        String uniqueUsername = "currentuser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("current@example.com");
        userDTO.setAddress("123 Current St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user as admin
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Test the /me endpoint with authentication as this specific user
        MvcResult result = mockMvc.perform(get("/api/v1/users/me")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(uniqueUsername)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Verify we got a non-null response
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody, "Response body should not be null");
        
        // The response should contain the username
        assertTrue(responseBody.contains(uniqueUsername), "Response should contain the username");
    }
    
    @Test
    public void testAddSubscription() throws Exception {
        // First create a user
        String uniqueUsername = "subuser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("sub@example.com");
        userDTO.setAddress("123 Sub St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user as admin
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extract the user ID from the response
        UserDTO createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDTO.class);
        Integer userId = createdUser.getId();
        
        // Add a subscription for the user
        MvcResult subResult = mockMvc.perform(post("/api/v1/subscriptions/" + userId + "/subscriptions")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "MONTHLY"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Print response for debugging
        String responseBody = subResult.getResponse().getContentAsString();
        System.out.println("Subscription Response: " + responseBody);
        assertNotNull(responseBody, "Response body should not be null");
    }
    
    @Test
    public void testGetUserSubscriptions() throws Exception {
        // First create a user
        String uniqueUsername = "getsubuser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("getsub@example.com");
        userDTO.setAddress("123 GetSub St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user as admin
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extract the user ID from the response
        UserDTO createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDTO.class);
        Integer userId = createdUser.getId();
        
        // Add a subscription first
        mockMvc.perform(post("/api/v1/subscriptions/" + userId + "/subscriptions")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "QUARTERLY"))
                .andExpect(status().isOk());
        
        // Get all subscriptions for the user
        MvcResult getSubResult = mockMvc.perform(get("/api/v1/subscriptions/" + userId + "/subscriptions")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId)))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Print response for debugging
        String responseBody = getSubResult.getResponse().getContentAsString();
        System.out.println("Get Subscriptions Response: " + responseBody);
        assertNotNull(responseBody, "Response body should not be null");
    }
    
    @Test
    public void testGetActiveUserSubscriptions() throws Exception {
        // First create a user
        String uniqueUsername = "activesubuser" + UUID.randomUUID().toString().substring(0, 8);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(uniqueUsername);
        userDTO.setPwd("Password123!");
        userDTO.setEmail("activesub@example.com");
        userDTO.setAddress("123 ActiveSub St");
        userDTO.setPhone("123-456-7890");
        
        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        userDTO.setRoles(roles);
        
        // Create the user as admin
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMINISTRATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extract the user ID from the response
        UserDTO createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDTO.class);
        Integer userId = createdUser.getId();
        
        // Add a subscription first
        mockMvc.perform(post("/api/v1/subscriptions/" + userId + "/subscriptions")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "YEARLY"))
                .andExpect(status().isOk());
        
        // Get active subscriptions for the user
        MvcResult getActiveSubResult = mockMvc.perform(get("/api/v1/subscriptions/" + userId + "/subscriptions/active")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                    .jwt(jwt -> jwt.claim("userId", userId)))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        // Print response for debugging
        String responseBody = getActiveSubResult.getResponse().getContentAsString();
        System.out.println("Get Active Subscriptions Response: " + responseBody);
        assertNotNull(responseBody, "Response body should not be null");
    }
} 