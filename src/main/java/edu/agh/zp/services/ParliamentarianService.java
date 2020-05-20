package edu.agh.zp.services;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.ParliamentarianEntity;
import edu.agh.zp.objects.PoliticianEntity;
import edu.agh.zp.repositories.ParliamentarianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ParliamentarianService {
    @Autowired
    ParliamentarianRepository pR;

    public ParliamentarianEntity create(ParliamentarianEntity parliamentarian){
        return pR.save(parliamentarian);
    }

    public ParliamentarianEntity update( ParliamentarianEntity parliamentarian ){
        return pR.save(parliamentarian);
    }

    public Optional<ParliamentarianEntity> findByParliamentarianID(Long parliamentarianID){
        return pR.findByParliamentarianID(parliamentarianID);
    }

    public Optional<ParliamentarianEntity> findByCitizen(CitizenEntity citizenId){
        return pR.findByPoliticianID_CitizenID(citizenId);
    }

    public Optional<ParliamentarianEntity> findByEmail(String email){ return pR.findByPoliticianID_CitizenID_Email(email);}

    public Optional<ParliamentarianEntity> findByPesel(String pesel) { return pR.findByPoliticianID_CitizenID_Pesel(pesel);}

    public ParliamentarianEntity findByPolitician(PoliticianEntity politicianID) { return pR.findByPoliticianID(politicianID);}

    public Optional<ParliamentarianEntity> findByIdNumber(String idNumber) { return pR.findByPoliticianID_CitizenID_IdNumber(idNumber);}

    public Optional<ParliamentarianEntity> findByParliamentarianIdNumber(String idNumber) { return pR.findByIdCardNumber(idNumber);}


    public ArrayList<ParliamentarianEntity> findAll(){
        return (ArrayList<ParliamentarianEntity>)pR.findAll();
    }

    public ArrayList<ParliamentarianEntity> findAllByChamberOfDeputies(String chamberOfDeputies){
        return (ArrayList<ParliamentarianEntity>)pR.findAllByChamberOfDeputies(chamberOfDeputies);
    }

    public ArrayList<ParliamentarianEntity> findAllByPoliticalGroup(String politicalGroup){
        return (ArrayList<ParliamentarianEntity>)pR.findAllByPoliticalGroup(politicalGroup);
    }

    public ArrayList<ParliamentarianEntity> findAllByChamberAndPoliticalGroup(String chamberOfDeputies, String politicalGroup){
        return (ArrayList<ParliamentarianEntity>)pR.findAllByChamberOfDeputiesAndPoliticalGroup(chamberOfDeputies, politicalGroup);
    }


}
