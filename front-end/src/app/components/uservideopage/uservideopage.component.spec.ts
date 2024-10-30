import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UservideopageComponent } from './uservideopage.component';

describe('UservideopageComponent', () => {
  let component: UservideopageComponent;
  let fixture: ComponentFixture<UservideopageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UservideopageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UservideopageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
