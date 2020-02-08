import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { WorkService } from '../services/Work/work.service';
import { EnumValue } from '../model/enum-value';
import { SessionStorageService } from '../services/SessionStorage/session-storage.service';

@Component({
  selector: 'app-add-new-work',
  templateUrl: './add-new-work.component.html',
  styleUrls: ['./add-new-work.component.scss']
})
export class AddNewWorkComponent implements OnInit {

  private choosingMagazineForm: FormGroup;
  private inputDetailsOfWorkForm:FormGroup;
  private coAuthorForm:FormGroup;
  private submitted = false;
  private success = false;

  private processInstance="";
  private formFieldsDto = null;
  private formFields = [];

  private enumKeys=[];
  private enumValues = [];
  private enumObjects=[];
  private enumObject:EnumValue;

  private formFields2=[];

  private enumKeys2=[];
  private enumValues2 = [];
  private enumObjects2=[];
  private enumObject2:EnumValue;

  private submitted2 = false;
  private success2 = false;

  private formFields3=[];
  private submitted3 = false;
  private success3 = false;
  private numOfCoAuthors=0;

  pdfFile:File=null;


  private firstPhaseFinished=false;
  private showCoauthors=false;

  constructor(private sessionStorageService:SessionStorageService,private formBuilder:FormBuilder, private router:Router, private workServcie:WorkService ) {

      this.workServcie.getChoosingMagazineForm().subscribe(
        res=>{
          console.log(res);
          this.formFieldsDto = res;
          this.formFields = res.formFields;
          this.processInstance = res.processInstanceId;
          this.sessionStorageService.saveProcessId(this.processInstance);
          this.formFields.forEach( (field) =>{
            
            if(field.type.name=='enum'){
              // console.log('usaoo');
              this.enumKeys =Object.keys(field.properties);
              this.enumValues =Object.values(field.properties);
              // console.log(this.enumKeys);
              for(let i=0; i<this.enumKeys.length; i++){
                this.enumObject=new EnumValue(this.enumKeys[i],this.enumValues[i]);
                this.enumObjects.push(this.enumObject);
              }
              // console.log(this.enumObjects);
              // this.enumHere=true;
              // console.log(this.enumValues);
            }
          });
        },
        error=>{
          console.log(error);
        }
      )
     }

  ngOnInit() {
    this.choosingMagazineForm=this.formBuilder.group({
      chooseMagazineForm:['',Validators.required]
    });

    this.inputDetailsOfWorkForm=this.formBuilder.group({
      titleWorkForm: ['',Validators.required],
      abstractWorkForm: ['', Validators.required],
      keyTermsWorkForm: ['',Validators.required],
      scienceAreaForm: ['',Validators.required],
      numberCoauthorsForm: ['0',Validators.required],
      pdfWorkForm: ['',Validators.required]
    });

    this.coAuthorForm=this.formBuilder.group({
      nameCoForm:['',Validators.required],
      surnameCoForm:['',Validators.required],
      emailCoForm: ['',Validators.required],
      countryCoForm: ['', Validators.required],
      cityCoForm: ['',Validators.required]
    });
  }

  chooseMagazine(value, form){
    this.submitted=true;

    if(this.choosingMagazineForm.invalid){
      return;
    }

    this.success=true;

    let o = new Array();

    for (var property in value) {
      if(property == 'chooseMagazineForm'){
        o.push({fieldId : 'choseMagazineForm', fieldValue : value[property]});

      }else{
        o.push({fieldId : property, fieldValue : value[property]});

      }
    }
    console.log(o);

    this.workServcie.postChoosingMagazine(this.processInstance,this.formFieldsDto.taskId,o).subscribe(
      res=>{
        if(res.message == 'ok'){
          this.firstPhaseFinished=true;
          this.getFormForWorkDetails();
        }else{
          alert("Nemate uplacenu clanarinu za ovaj casopis. Molim najpre izvrsite uplatu");
        }
      },
      error=>{
        console.log(error);
      }
    )
  }

  getFormForWorkDetails(){
  
    this.workServcie.getFormForWorkDetails(this.processInstance).subscribe(
      res=>{
        console.log(res);
          this.formFieldsDto = res;
          this.formFields2 = res.formFields;
          this.processInstance = res.processInstanceId;
          this.formFields2.forEach( (field) =>{
            
            if(field.type.name=='enum'){
              // console.log('usaoo');
              this.enumKeys2 =Object.keys(field.properties);
              this.enumValues2 =Object.values(field.properties);
              // console.log(this.enumKeys);
              for(let i=0; i<this.enumKeys2.length; i++){
                this.enumObject2=new EnumValue(this.enumKeys2[i],this.enumValues2[i]);
                this.enumObjects2.push(this.enumObject2);
              }
              // console.log(this.enumObjects);
              // this.enumHere=true;
              // console.log(this.enumValues);
            }
          });
      },
      error => {
        console.log(error);
      }
    );
  }

  sendMagazineData(value, form){
    this.submitted2=true;

    if(this.inputDetailsOfWorkForm.invalid){
      return;
    }

    this.success2=true;

    let o = new Array();

    for (var property in value) {
      if(property == 'scienceAreaForm'){
        o.push({fieldId : 'scienceAreaForma', fieldValue : value[property]});
      }else if(property == 'pdfWorkForm'){
        //o.push({fieldId : 'pdfWorkForma', fieldValue : this.pdfFile});
      }else{
        o.push({fieldId : property, fieldValue : value[property]});
      }
    }

    this.numOfCoAuthors=this.inputDetailsOfWorkForm.get('numberCoauthorsForm').value;
    console.log('Num of coauthors'+this.numOfCoAuthors);
    this.workServcie.postFormForWorkDetails(this.processInstance,this.formFieldsDto.taskId,o).subscribe(
      res=>{
        if(res.message == 'ok'){
          this.workServcie.sendFile(this.pdfFile,this.processInstance).subscribe(
            res=>{
            console.log('Num2 of coauthors'+this.numOfCoAuthors);
  
              if(this.numOfCoAuthors != 0){
                this.showCoauthors=true;
                this.firstPhaseFinished=false;
                this.getCoAuthorsForm();
              }else{
                alert('Vas rad je poslat na razmatranje.');
                this.router.navigate(['/']);
              }
              
            },
            error=>
            {
              console.log(error);
            }
          )
        }else{
          alert("Greska");
        }
      },
      error=>{
        console.log(error);
      }
    )
  }

  pdfSelected(event){
    this.pdfFile=<File>event.target.files[0];
    console.log(event);
  }

  getCoAuthorsForm(){
    this.workServcie.getCoAuthorsForm(this.processInstance).subscribe(
      res=>{
        this.formFieldsDto = res;
        this.formFields3 = res.formFields;
        this.processInstance = res.processInstanceId;
        this.numOfCoAuthors=this.numOfCoAuthors-1;     
      },
      error=>{
        console.log(error);
      }
    )
  }

  postCoauthor(value, form){

    this.submitted3=true;

    if(this.coAuthorForm.invalid){
      return;
    }

    this.success3=true;

    let o = new Array();

    for (var property in value) {
        o.push({fieldId : property, fieldValue : value[property]});
    }

    this.workServcie.postCoAuthorForm(this.processInstance,this.formFieldsDto.taskId,o).subscribe(
      res=>{
        if(this.numOfCoAuthors > 0){
          this.submitted3=false;
          this.coAuthorForm.reset();
          this.getCoAuthorsForm();
        }else{
          alert('Dodali ste koautore');
          this.router.navigate(['/']);
        }
      },
      error=>{
        console.log(error);
      }
    );
  }


}