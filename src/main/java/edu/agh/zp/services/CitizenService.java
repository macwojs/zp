package edu.agh.zp.services;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.repositories.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class CitizenService {

    @Autowired
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