/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nearsoft.academy.bigdata.recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author rdoja
 */
public class MovieRecommender {
    
    private int totalReviews=0;
    private int totalProducts=0;
    private int totalUsers=0;
    
    private int ProductNumber;
    private int UserNumber;
    
    private final HashMap<String, Integer> usersHash = new HashMap();
    private final HashMap<String, Integer> productsHash = new HashMap();
    private final HashMap<Integer, String> InverseProductsHash = new HashMap();

    MovieRecommender(String path)throws IOException, TasteException {
        
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        File reviews = new File("reviews.csv");
        FileWriter fw = new FileWriter(reviews);
        BufferedWriter wr = new BufferedWriter(fw);
        
        
        String userId = "", productId = "", score;
        String line;
        
        while ((line = br.readLine()) != null) {
            
            if("product/productId:".equals(line.split(" ")[0])){
                productId = line.split(" ")[1];
                if(productsHash.containsKey(productId) != true) {
                    totalProducts++;
                    productsHash.put(productId,totalProducts);
                    InverseProductsHash.put(totalProducts,productId);
                    ProductNumber = totalProducts;
                  }
                else{
                    ProductNumber = productsHash.get(productId);
                  }
            }
            if("review/userId:".equals(line.split(" ")[0])){
                userId = line.split(" ")[1];
                if(usersHash.containsKey(userId) != true) {
                    totalUsers++;
                    usersHash.put(userId,totalUsers);
                    UserNumber = totalUsers;
                  }
                else{
                    UserNumber = usersHash.get(userId);
                  } 
            }
            
            if("review/score:".equals(line.split(" ")[0])){
                score = line.split(" ")[1];
                wr.write(UserNumber+","+ProductNumber+","+score+"\n");
                totalReviews++;
            }
          }
        br.close();
        wr.close();
    }            
            
            
    public int getTotalReviews(){
        return totalReviews;
    }
    public int getTotalProducts(){
        return totalProducts;
    }
    public int getTotalUsers(){
        return totalUsers;
    }

    List<String> getRecommendationsForUser(String UserId) throws IOException, TasteException{
        
        DataModel model = new FileDataModel(new File("reviews.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        
        List <RecommendedItem> recommendations = recommender.recommend(usersHash.get(UserId), 3);
        
        List<String> Response = new ArrayList <String>();
        
        for (RecommendedItem recommendation : recommendations) {
            Response.add(InverseProductsHash.get((int)recommendation.getItemID()));
        }
        
        return Response;
    }
}