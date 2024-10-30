import { Component, EventEmitter, Output } from '@angular/core';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { FormsModule } from '@angular/forms';
import { DescriptionDTO } from '../../models/description-dto';

@Component({
  selector: 'app-user-descrption',
  standalone: true,
  imports: [MdbFormsModule, FormsModule],
  templateUrl: './user-descrption.component.html',
  styleUrl: './user-descrption.component.scss'
})
export class UserDescrptionComponent {

  description: DescriptionDTO = new DescriptionDTO();
  @Output("descriptionRetorno") descriptionRetorno = new EventEmitter<any>();

  postDescription(){
    this.descriptionRetorno.emit(this.description);
  }

}
