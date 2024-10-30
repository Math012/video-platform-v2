import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserDescrptionComponent } from './user-descrption.component';

describe('UserDescrptionComponent', () => {
  let component: UserDescrptionComponent;
  let fixture: ComponentFixture<UserDescrptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserDescrptionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserDescrptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
