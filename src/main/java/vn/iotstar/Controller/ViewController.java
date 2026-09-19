package vn.iotstar.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/admin/category")
    public String categoryPage() {
        return "admin/category";
    }

    @GetMapping("/admin/product")
    public String productPage() {
        return "admin/product";
    }
}
