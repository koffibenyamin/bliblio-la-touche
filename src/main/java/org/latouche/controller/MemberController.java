package org.latouche.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.latouche.model.Member;
import org.latouche.model.Order;
import org.latouche.parameters.QRCodeGenerator;
import org.latouche.repository.MemberRepository;
import org.latouche.repository.OrderRepository;
import org.latouche.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import jakarta.servlet.http.HttpServletResponse;

import com.itextpdf.layout.element.Image;
import java.io.OutputStream;



@Controller
@RequestMapping("/member")
public class MemberController {
	
	private final MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private QRCodeGenerator qrCodeGenerator;
	
	@Autowired
	private OrderRepository orderRepository;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	
    ////
    ///
    @GetMapping("")
    public String showAllMembers(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        model.addAttribute("member", new Member());
        model.addAttribute("memberToUpdate", new Member());
        return "member"; // your Thymeleaf page
    }

    // Get one member as JSON (for filling the update form via JavaScript)
    @ResponseBody
    @GetMapping("/get/{id}")
    public Member getMemberByIdoo(@PathVariable Long id) {
        return memberService.getMemberById(id).orElse(null);
    }

    // Create a new member
    @PostMapping("/create")
    public String createMember(@ModelAttribute Member member) {
        memberService.createMember(member);
        return "redirect:/member";
    }

    // Update existing member by ID
     
    @PostMapping("/update")
    public String updateMember(@ModelAttribute("memberToUpdate") Member member) {
        memberService.updateMember(member.getIdPerson(), member);
        return "redirect:/member";
    }


    // Delete a member
    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return "redirect:/member";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Optional<Member> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }
    
    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id) {
        memberService.toggleStatus(id); // this will toggle or change the status
        return "redirect:/member";
    }
    
    @GetMapping("/member/qrcode/{registerNumber}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable String registerNumber) throws WriterException, IOException {
        BufferedImage qrImage = qrCodeGenerator.generateQRCodeImage(registerNumber, 200, 200);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/card/{id}")
    public void generateMemberCardImage(@PathVariable Long id, HttpServletResponse response) throws IOException, WriterException {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member not found");
            return;
        }

        Member member = memberOptional.get();

        // Format date safely
        String formattedDate = member.getDateOfBirth() != null
            ? member.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            : "N/A";

        // Generate QR Code
        BufferedImage qrCode = qrCodeGenerator.generateQRCodeImage(member.getRegisterNumber(), 100, 100);

        // Create card
        int width = 700;
        int height = 400;
        BufferedImage card = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = card.createGraphics();

        // Background
        g.setColor(new Color(173, 216, 230)); // Light blue
        g.fillRect(0, 0, width, height);

        // Logo
        InputStream logoStream = getClass().getResourceAsStream("/static/image/logo.png");
        if (logoStream != null) {
            BufferedImage logo = ImageIO.read(logoStream);
            g.drawImage(logo, 20, 20, 100, 100, null);
        }

        // Titles
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("La Touche", 250, 50);

        g.setFont(new Font("Serif", Font.ITALIC, 18));
        g.drawString("Touch and Impact", 250, 80);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("CARTE DE MEMBRE", 220, 120);

        // QR Code
        g.drawImage(qrCode, width - 120, 20, null);

        // Member info
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Nom Complet: " + member.getNamePerson() + " " + member.getFirstNamePerson(), 50, 200);
        g.drawString("Téléphone: " + member.getPhoneNumber(), 50, 240);
        g.drawString("Date de Naissance: " + formattedDate, 50, 280);
        g.drawString("Matricule: " + member.getRegisterNumber(), 50, 320);

        g.dispose();

        // Write image to response
        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "attachment; filename=carte_membre_" + member.getRegisterNumber() + ".png");

        OutputStream out = response.getOutputStream();
        ImageIO.write(card, "png", out);
        out.close();
    }
    
    
    @GetMapping("/{id}/details")
    public String getMemberWithBorrowedBooks(@PathVariable Long id, Model model) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            return "error/404"; // Or handle as needed
        }

        Member member = memberOptional.get();
        List<Order> orders = orderRepository.findOrdersByMemberId(id);

        model.addAttribute("member", member);
        model.addAttribute("orders", orders);

        return "member-info"; // Thymeleaf view
    }



}
