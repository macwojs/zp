package edu.agh.zp.services;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.hibernate.CitizenRepository;

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
