import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const url='http://localhost:8081/editorWork/';
@Injectable({
  providedIn: 'root'
})
export class EditorWorkService {
  
  private getAllArrivedWorksUrl=url+"getArrivedWorks";
  private getCheckWorkFormUrl=url+"getCheckWorkForm/";
  private approveWorkUrl=url+"detailsCorrect/";
  private denyWorkUrl=url+"detailsDenied/";
  
  private getAllWorksForPdfCheckUrl=url+"worksForPdfCheck";
  private approvePdfUrl=url+"approvePdf/";
  private needToFixPdfUrl=url+"needToFixPdf/";
  private sendCommentForPdfUrl=url+"sendCommentForPdf/";
  
  constructor(private http:HttpClient) { }

  getArrivedWorks():Observable<any>{
    return this.http.get(this.getAllArrivedWorksUrl);
  }

  getCheckWorkForm(processId):Observable<any>{
    return this.http.get(this.getCheckWorkFormUrl+processId);
  }

  approveWorkDetails(processId,workId):Observable<Blob>{
    let url=this.approveWorkUrl+processId+"/"+workId;
    // const httpOptions = {
    //   headers: new HttpHeaders({ 'Content-Type': 'application/pdf', "responseType": 'application/pdf' })
    // };
    // return this.http.get<String>(url,httpOptions);
    return this.http.get(url, {responseType: 'blob'});
    //return this.http.get(this.approveWorkUrl+processId+"/"+workId,headers);
  }

  denyWorkDetails(processId,workId):Observable<any>{
    return this.http.get(this.denyWorkUrl+processId+"/"+workId);
  }

  getWorksForPdfCheck():Observable<any>{
    return this.http.get(this.getAllWorksForPdfCheckUrl);
  }

  approvePdf(workId):Observable<any>{
    return this.http.get(this.approvePdfUrl+workId);
  }

  getCommentForm(processId):Observable<any>{
    return this.http.get(this.needToFixPdfUrl+processId);
  }

  sendPdfToFix(processId,taskId,o):Observable<any>{
    return this.http.post(this.sendCommentForPdfUrl+processId+"/"+taskId,o);
  }
}
