package edu.agh.zp.services;

import edu.agh.zp.hibernate.CitizenEntity;
import edu.agh.zp.hibernate.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;

public class CitizenService {
    private CitizenRepository cR;

    public CitizenService(CitizenRepository cR){
        this.cR = cR;
    }

    public CitizenEntity create(CitizenEntity citizen){
        return cR.save(citizen);
    }

    public ArrayList<CitizenEntity> findAll(){
        return (ArrayList<CitizenEntity>)cR.findAll();
    }

    public CitizenEntity findById(Long id){
        return cR.findByCitizenID(id);
    }

    public CitizenEntity update( CitizenEntity citizen ){
        return cR.save(citizen);
    }
}
