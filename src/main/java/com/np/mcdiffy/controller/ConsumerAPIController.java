package com.np.mcdiffy.controller;

import com.np.mcdiffy.entities.Cart;
import com.np.mcdiffy.entities.CartProduct;
import com.np.mcdiffy.entities.Product;
import com.np.mcdiffy.entities.UserDetails;
import com.np.mcdiffy.repositories.CartProductRepo;
import com.np.mcdiffy.repositories.CartRepo;
import com.np.mcdiffy.repositories.UserDetailsRepo;
import com.np.mcdiffy.utilities.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ConsumerAPIController {

    private final JwtUtility jwtUtility;
    private final UserDetailsRepo userRepo;
    private final CartRepo cartRepo;
    private final CartProductRepo cartProductRepo;

    @GetMapping("api/auth/consumer/cart")
    public ResponseEntity<?> getConsumerCart(@RequestHeader("JWT") String jwt) {
        String userName = jwtUtility.extractUserName(jwt);
        Optional<UserDetails> user = userRepo.findByUsername(userName);
        if (user.isPresent()) {
            Optional<Cart> cart = cartRepo.findByUserUsername(user.get().getUsername());
            if (cart.isPresent()) {
                return ResponseEntity.ok(cart.get());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Couldn't found any cart details for user");
    }

    @PostMapping("api/auth/consumer/cart")
    public ResponseEntity<?> addProductToConsumerCart(@RequestHeader("JWT") String jwt, @RequestBody Product product) {
        String userName = jwtUtility.extractUserName(jwt);
        try {
            //Find the user's cart details
            Optional<Cart> userCart = cartRepo.findByUserUsername(userName);
            //Get the cartProducts from cart
            Cart cart = userCart.orElseThrow();
            List<CartProduct> cartProducts = cart.getCartProducts();
            //Check if the given product is present in it
            boolean isProductPresent = cartProducts.stream().anyMatch(cp -> cp.getProduct()
                    .getProductName().equalsIgnoreCase(product.getProductName()));
            if (!isProductPresent) {
                //Create a new Cart Product with given product and save it to cp repo
                CartProduct cartProduct = new CartProduct();
                cartProduct.setQuantity(1);
                cartProduct.setProduct(product);
                cartProduct.setCart(cart);
                //Add it to cart
                cartProducts.add(cartProduct);
                cart.setTotalAmount(cart.getTotalAmount() + product.getPrice());
                cart.setCartProducts(cartProducts);
                //Save cartRepo
                cartRepo.save(cart);
                return ResponseEntity.ok("Product saved to cart");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT.value()).body("Product already in cart");
            }
        } catch (Exception e) {
            System.out.println("Exception : "+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or cart details not found");
        }
    }

    @PutMapping("api/auth/consumer/cart")
    public ResponseEntity<?> updateProductQuantityInCart(@RequestHeader("JWT") String jwt, @RequestBody CartProduct cartProduct) {
        //A. Check if product in cart, if not present, then add else update qty and price
        try {
            String userName = jwtUtility.extractUserName(jwt);
            //1. Finding Product in cart - Steps
            Optional<Cart> userCart = cartRepo.findByUserUsername(userName);
            //2. Get the consumer's cart
            Cart cart = userCart.orElseThrow();
            //3. Search the cartProducts for the particular product
            boolean isCartProductInCart = cart.getCartProducts().stream().anyMatch(cp -> cp.getProduct().getProductName()
                    .equalsIgnoreCase(cartProduct.getProduct().getProductName()));
            //Setting cart to cartProduct, so it'll be properly linked while updating in db
            if (isCartProductInCart) {
                //C. If input qty is zero, delete product from cart
                if (cartProduct.getQuantity() == 0) {
                    //Get the existing qty of the product in cart from cartProducts
                    List<CartProduct> cartProducts = cart.getCartProducts();
                    CartProduct cpFirst = cartProducts.stream().filter(cp -> {
                                if (cp.getProduct().getProductId() == cartProduct.getProduct().getProductId()) {
                                    return true;
                                }
                                return false;
                            }
                    ).findFirst().orElseThrow();
                    //If input-quantity is zero, update totalAmt in cart and save it to cart object
                    //For Bidirectional linkage, detach both sides and delete
                    cart.setTotalAmount(cart.getTotalAmount() - (cpFirst.getQuantity() * cpFirst.getProduct().getPrice()));
                    cart.getCartProducts().remove(cpFirst);
                    cartRepo.save(cart);
                    //Delete the product from cartProduct
                    cartProductRepo.deleteById(cpFirst.getCpId());

                    return ResponseEntity.ok("Quantity is zero, product deleted from cart");
                }
                //Else we need to first update the quantity of product in cartProduct object
                List<CartProduct> cartProducts = cart.getCartProducts();
                //Here we're getting the required cartProduct object from the cart object using stream filter
                CartProduct cpFirst = cartProducts.stream().filter(cp -> {
                            if (cp.getProduct().getProductId() == cartProduct.getProduct().getProductId()) {
                                return true;
                            }
                            return false;
                        }
                ).findFirst().orElseThrow();
                //Now we're updating the cp object quantity and updating it in cart repo
                cpFirst.setQuantity(cartProduct.getQuantity());
                cartProductRepo.save(cpFirst);
                //Now doing a separate save for cart object with quantity details alone and not attaching any
                // cartProducts to it
                cart.setTotalAmount(cart.getTotalAmount() + (cartProduct.getQuantity() * cartProduct.getProduct().getPrice()));
                cartRepo.save(cart);
                return ResponseEntity.ok("Product is present in cart, Updated totalAmt and qty");
            }

            //This is required only if we're adding a new cart object, one which is not already present
            cartProduct.setCart(cart);
            //CartProduct is not present in cart --> Simply add it to existing list and do an update.
            //Update list and total amount in cart
            List<CartProduct> cps = cart.getCartProducts();
            cps.add(cartProduct);
            cart.setCartProducts(cps);
            cart.setTotalAmount(cart.getTotalAmount() + (cartProduct.getQuantity() * cartProduct.getProduct().getPrice()));
            cartRepo.save(cart);
            return ResponseEntity.ok("Product is not-present in cart, Added it to cart, updated totalAmt");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or cart details not found");
        }
    }

    @DeleteMapping("api/auth/consumer/cart")
    public ResponseEntity<?> deleteFromCart(@RequestHeader("JWT") String jwt, @RequestBody Product product) {
        try {
            String userName = jwtUtility.extractUserName(jwt);
            //1. Finding Product in cart - Steps
            Optional<Cart> userCart = cartRepo.findByUserUsername(userName);
            //2. Get the consumer's cart
            Cart cart = userCart.orElseThrow();
            List<CartProduct> cartProducts = cart.getCartProducts();
            CartProduct deleteCp = cartProducts.stream().filter(cp -> {
                        if (cp.getProduct().getProductId() == product.getProductId()) {
                            return true;
                        }
                        return false;
                    }
            ).findFirst().orElseThrow();
            //If input-quantity is zero, update totalAmt in cart and save it to cart object
            //For Bidirectional linkage, detach both sides and delete
            cart.setTotalAmount(cart.getTotalAmount() - (deleteCp.getQuantity() * deleteCp.getProduct().getPrice()));
            cart.getCartProducts().remove(deleteCp);
            cartRepo.save(cart);
            //Delete the product from cartProduct
            cartProductRepo.deleteById(deleteCp.getCpId());

            return ResponseEntity.ok("Product deleted from cart");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Some error occurred in deleting");
        }

    }
}
