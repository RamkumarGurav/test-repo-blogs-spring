package com.ram.testforgithubandcyclic.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {



    //jwt needs minimum 256-bit key,online generate a 256-bit BASE64 hexademical key
    @Value("${jwtSecretKey}")
    String SECRET_JWT_KEY;


    // step1 - getSignInkey method returns the signinkey that is requered for generating the token and extracting claims from the token,
    // This private method is used to obtain a cryptographic key for signing JWTs.
    // This private method is used to obtain a cryptographic key for signing JWTs.
    // The JWT will be signed using a secret key, and this key is generated from
    // a provided Base64 encoded secret key string.
    private Key getSignInKey() {
        log.info("******:SECRET_JWT_KEY "+SECRET_JWT_KEY);
        //  1: Decode the secret key from Base64 representation to a byte array.
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_JWT_KEY);

        //  2: Create a Key object for HMAC (Hash-based Message Authentication Code)
        // using the decoded key bytes. HMAC is a symmetric cryptographic algorithm
        // used for creating and verifying message authentication codes.
        // The Keys.hmacShaKeyFor() method is assumed to be part of a cryptographic library.
        Key hmacKey = Keys.hmacShaKeyFor(keyBytes);

        //  3: Return the generated HMAC key, which will be used for signing JWTs.
        // The generated HMAC key will be used as the signing key when generating JWTs.
        return hmacKey;
    }






    //-------------------validating  given token-------------------------------

        //step2 - we need to get the userName(email) that is stored in jwt token's subject field so that we can
        //generate the useDetails using that email
        public Claims extractAllClaimsFromToken(String token){
//trick to remember - jpsb-pg
//---------------------------detailed-----------------------
//            // Step 1: Create a JWT parser builder to parse the token.
//            JwtParserBuilder parserBuilder = Jwts.parserBuilder();
//
//            // Step 2: Set the signing key for the parser.
//            // The signing key is used to verify the token's signature.
//            // Here, the getSignInKey() method is used to obtain the HMAC key for signing and verifying JWTs.
//            JwtParser parser = parserBuilder.setSigningKey(getSignInKey()).build();
//
//            // Step 3: Parse the token and retrieve the Jws (Json Web Signature) object.
//            // The Jws object contains the token's header, payload, and signature.
//            Jws<Claims> jws = parser.parseClaimsJws(token);
//
//            // Step 4: Get the claims (payload) from the Jws object.
//            // The claims include information such as user details, expiration time, etc.
//            Claims claims = jws.getBody();
//
//            // Return the extracted claims from the token.
//            return claims;
//-------------------------------------------------------------

            return Jwts
                    .parserBuilder().
                    setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }


    //step3 - making extractClaim method that helps to extract one claim using extractAllClaims method
        public <T> T extractClaimFromToken(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    //step4 - extracting username(ie email) which is in subject (one of the claims)
    //In this method, the extractClaimFromToken method is used to extract a specific claim from the
    // JWT payload. The specific claim being extracted is the "subject" claim, which typically holds the user's
    // identifier (in this case, the email). To extract the subject claim, a lambda function is passed to the
    // extractClaimFromToken method. The lambda function (claims -> claims.getSubject()) takes a Claims object as
    // input and returns the subject claim from it using the getSubject() method.
    //
    //In summary, the use of Function<Claims, T> allows you to encapsulate the logic of extracting a specific
    // claim from the JWT payload and pass it as a parameter to a method. This makes the code more flexible and
    // reusable, as you can easily change the claim you want to extract by providing a different lambda function
    // to the extractClaimFromToken method.
    public String extractUsernameFromToken(String token) {
        log.info("********: Inside isTokenExpired method");
        return  extractClaimFromToken(token,claims -> claims.getSubject());
//      return  extractClaim(token,Claims::getSubject);


    }

    //step4 - extracting token expiration date which is from the given token
    public Date extractExpirationFromToken(String token) {
        return  extractClaimFromToken(token,claims -> claims.getExpiration());

    }




    //step5-checking if the token is expired or not
    public boolean isTokenExpired(String token){
        log.info("********: Inside isTokenExpired method");
    Date tokenExpirationDate =extractExpirationFromToken(token);
    //if the token's expiration date is before present date then that
        // token is expired or else it is not expired
     return tokenExpirationDate.before(new Date());

    }


    //step6 -Checking if given token valid (if it has same username as the username stored in
    // the userDetails that we got from the DB
    public boolean isTokenValid(String token,UserDetails userDetails){
        log.info("********: Inside isTokenValid method");
    final String usernameInsideToken = extractUsernameFromToken(token);
    return usernameInsideToken.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

//--------------Generating new Token-------------------------------------------


    //step1 - generating/building jwt token with validity of 90 days
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.info("********: Inside generateToken method");
        return Jwts.builder()
                .addClaims(extraClaims) // Adding any additional claims to the JWT payload
                .setSubject(userDetails.getUsername()) // Setting the subject (typically the user's identifier, like email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Setting the issued timestamp
                .setExpiration(new Date(System.currentTimeMillis()+(1000*60*60*24*90L))) // Setting the expiration timestamp (90 days from now)(its in
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Signing the JWT with the HMAC key using the HS256 algorithm
                .compact(); // Building the JWT and compacting it into a string
    }

    //step2 - generating jwt toke only using userName of userDetails without
    // passing extraClaims(Extra info in jwt header)
    //this method is used to generate token while executing singup/register method in the AuthController
    public String generateToken(UserDetails userDetails){
//The reason for using an empty HashMap as extraClaims is that during user registration, you might not have any
// additional information that you want to include in the token's payload. The generateToken method supports the
// option to include additional claims if needed. By passing an empty HashMap, you're effectively indicating that
// no extra claims are necessary for this specific use case (user registration). The token is generated solely
// based on the user's details, such as their username.
        return generateToken(new HashMap<>(),userDetails);
    }





}
