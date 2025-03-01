package mtcg.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mtcg.repository.PackageRepository;
import mtcg.repository.UserRepository;
import mtcg.repository.CardRepository;
import mtcg.model.Card;
import mtcg.model.User;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PackageService {
    private PackageRepository packageRepo = new PackageRepository();
    private UserRepository userRepo = new UserRepository();
    private CardRepository cardRepo = new CardRepository();
    private ObjectMapper mapper = new ObjectMapper();

    public boolean createPackage(String json) {
        try {
            CardDefinition[] cards = mapper.readValue(json, CardDefinition[].class);
            // Jede Package-Aktion enthält 5 Karten
            for (CardDefinition cd : cards) {
                packageRepo.addPackageCard(cd);
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean acquirePackage(String username) {
        User user = userRepo.getUser(username);
        if (user == null || user.getCoins() < 5)
            return false;
        List<Card> pkg = packageRepo.acquirePackage();
        if (pkg == null || pkg.isEmpty())
            return false;
        user.setCoins(user.getCoins() - 5);
        userRepo.updateUser(user);
        for (Card card : pkg) {
            card.setUserId(user.getId());
            cardRepo.addCard(card);
        }
        return true;
    }

    public static class CardDefinition {
        @JsonProperty("Id")
        private String Id;
        @JsonProperty("Name")
        private String Name;
        @JsonProperty("Damage")
        private double Damage;
        public String getId() { return Id; } public void setId(String id) { this.Id = id; }
        public String getName() { return Name; } public void setName(String name) { this.Name = name; }
        public double getDamage() { return Damage; } public void setDamage(double damage) { this.Damage = damage; }
    }
}
