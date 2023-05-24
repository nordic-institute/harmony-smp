import {Component, OnInit} from '@angular/core';
import {SmpInfoService} from '../../app-info/smp-info.service';
import {SmpInfo} from '../../app-info/smp-info.model';

@Component({
  moduleId: module.id,
  templateUrl: './footer.component.html',
  selector: 'footer',
  styleUrls: ['./footer.component.css']
})

export class FooterComponent implements OnInit {
  smpVersion: string;

  constructor(private smpInfoService: SmpInfoService) {
  }

  ngOnInit(): void {

    console.log("FooterComponent onInit");
    this.smpInfoService.getSmpInfo().subscribe((smpInfo: SmpInfo) => {
      this.smpVersion = smpInfo.version;
      }
    );
  }
}
