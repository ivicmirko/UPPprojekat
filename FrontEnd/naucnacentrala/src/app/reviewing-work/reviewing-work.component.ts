import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ReviewerService } from '../services/Reviewer/reviewer.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EnumValue } from '../model/enum-value';

@Component({
  selector: 'app-reviewing-work',
  templateUrl: './reviewing-work.component.html',
  styleUrls: ['./reviewing-work.component.scss']
})
export class ReviewingWorkComponent implements OnInit {

  private showList=true;
  private processInstance="";
  private formFieldsDto = null;
  private formFields = [];
  private works=[];
  private processId:String;
  private workId:Number;
  private title="";
  private reviewingWorkForm:FormGroup;

  private enumKeys=[];
  private enumValues = [];
  private enumObjects=[];
  private enumObject:EnumValue;

  private submitted=false;
  private success=false;

  constructor(private router:Router,private reviewerService:ReviewerService,private formBuilder:FormBuilder) { }

  ngOnInit() {

    this.reviewingWorkForm=this.formBuilder.group({
      commentForAuthor:['',Validators.required],
      commentForEditor:['',Validators.required],
      recommendation:['',Validators.required]
    });

    this.reviewerService.getWorksForReviewing().subscribe(
      res=>{
        this.works=res;
        console.log(res);
      },
      error=>{
        console.log(error);
      }
    );
  }

  getReviewingForm(workId,processId){
    this.workId=workId;
    this.processId=processId;
    this.reviewerService.getReviewingForm(processId).subscribe(
      res=>{
        this.showList=false;
        console.log(res);
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) =>{
            
          if(field.type.name=='enum'){
            this.enumKeys =Object.keys(field.type.values);
            this.enumValues =Object.values(field.type.values);
            console.log(this.enumKeys);
            for(let i=0; i<this.enumKeys.length; i++){
              this.enumObject=new EnumValue(this.enumKeys[i],this.enumValues[i]);
              this.enumObjects.push(this.enumObject);
            }
            console.log(this.enumObjects);
          }
        });
      },
      error=>{
        console.log(error);
      }
    );
  }

  postReviewingForm(value,form){

    this.submitted=true;

    if(this.reviewingWorkForm.invalid){
      return;
    }

    this.success=true;

    let o = new Array();

    for (var property in value) {
      o.push({fieldId : property, fieldValue : value[property]});
    }

    this.reviewerService.postReviewingForm(this.processId,this.formFieldsDto.taskId,o).subscribe(
      res=>{
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.router.onSameUrlNavigation = 'reload';
        this.router.navigateByUrl('/reviewingWorks');
      },
      error=>{
        console.log(error);
      }
    )
    
  }

  downloadPdf(workId,processId,title){

    this.title=title;
    this.reviewerService.downloadPdf(processId,workId).subscribe(
      res=>{
        console.log('DOwnload PDF');
        console.log(res);
        let blob = new Blob([res], { type: 'blob' });
        let url= window.URL.createObjectURL(res);
        
        var link = document.createElement('a');
            link.href = url;
            link.download = this.title+".pdf";
            // this is necessary as link.click() does not work on the latest firefox
            link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));
        
      },
      error=>{
        console.log(error);
      }
    )
  }

}
